package org.jigsaw.payment.rpc.sharder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.x.discovery.ServiceInstance;
import org.jigsaw.payment.rpc.register.RpcPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 最简单的连接池实现：<ol>
 * <li>正常情况下，采用round-robin方式，轮流使用不同的server。</li>
 * <li>如果一个transport出现问题，则对它进行错误计数。如果是短暂的链接问题，短时间内恢复了，
 * 则继续正常使用。如果计数器超过一定的阈值，则标记为不可用，不再参与分配。</li>
 * <li>当server从zookeeper上被移除，则对这个server也从缓存中删除，不再参与分配transport.</li>
 * </ol>
 * <p>通用链接池可配置参数：
 * <ol>
 * <li>maxTotal: 最大总链接数</li>
 * <li>maxIdlePerKey: 每台机器的最大空闲连接数，如果超过这个数，空闲链接将被释放。 </li>
 * <li>maxWaitMillis: 最大等待时间， 如果在这个时间内取不到链接，则判断为失败。</li>
 * </ol>
 * </p>
 * <p>Basic链接池可配置参数：<ol>
 * <li>capacity : 可用的server队列的最多个数</li>
 * <li>failedMaxCount:
 * 在failedTestSpan期间，发生的错误超过failedMaxCount次数，则将server标记为不可用。</li>
 * <li>failedTestSpan:
 * 在failedTestSpan期间，发生的错误超过failedMaxCount次数，则将server标记为不可用</li>
 * <li>takeTimeoutMs: 从server上分配链接的等待时间。超过这个时间会标记为无法获取到链接.</li>
 * </ol>
 * </p>
 *
 * @author shamphone@gmail.com
 * @version 1.0.0
 **/
public class BasicTransportPool extends RefreshableTransportPool {
    private static Logger LOG = LoggerFactory.getLogger(BasicTransportPool.class);
    /**
     * 可用的server队列的最多个数
     */
    protected int capacity = 2048;
    /**
     * 在failedTestSpan期间，发生的错误超过failedMaxCount次数，则将server标记为不可用。
     */
    protected int failedMaxCount = 5;
    /**
     * 在failedTestSpan期间，发生的错误超过failedMaxCount次数，则将server标记为不可用。
     */
    protected int failedTestSpan = 1000;

    protected static final int MAX_SERVICE_INSTANCES = 1024;

    /**
     * 每个实例的错误次数。
     */
    protected Cache<ServiceInstance<RpcPayload>, AtomicInteger> failedCount;
    /**
     * Callable for cache. it will create a zero AtomicInteger object.
     */
    protected Callable<AtomicInteger> failedCountCallable;
    /**
     * 从server上分配链接的等待时间。超过这个时间会标记为无法获取到链接。
     */
    protected int takeTimeoutMs = 10000;

    /**
     * 现有的实例列表
     */
    protected BlockingQueue<ServiceInstance<RpcPayload>> instances;

    public BasicTransportPool() {
    }

    @Override
    public void start() throws Exception {
        this.instances = new ArrayBlockingQueue<ServiceInstance<RpcPayload>>(
                capacity);
        this.failedCount = CacheBuilder.newBuilder().concurrencyLevel(4)
                .weakKeys().maximumSize(MAX_SERVICE_INSTANCES)
                .expireAfterWrite(failedTestSpan, TimeUnit.MILLISECONDS)
                .build();
        this.failedCountCallable = new Callable<AtomicInteger>() {

            @Override
            public AtomicInteger call() throws Exception {
                return new AtomicInteger(0);
            }

        };
        super.start();
    }

    @Override
    protected void onInstanceAdded(ServiceInstance<RpcPayload> instance) {
        if (!this.instances.contains(instance)) {
            this.instances.offer(instance);
            this.failedCount.put(instance, new AtomicInteger(0));
            LOG.info("Add an instance to pool:  " + instance);
        }
    }

    @Override
    protected void onInstanceRemoved(ServiceInstance<RpcPayload> instance) {
        if (this.instances.contains(instance)) {
            this.instances.remove(instance);
            LOG.info("Instance removed from pool : " + instance);
        }
        this.failedCount.invalidate(instance);
    }

    /**
     * choose an instance from current instance set;
     * @return
     */
    @Override
    protected ServiceInstance<RpcPayload> chooseInstance() {
        ServiceInstance<RpcPayload> instance = null;
        boolean isAvailable = false;
        try {
            int tries = 0;
            do {
                try {
                    instance = this.instances.poll(this.takeTimeoutMs,
                            TimeUnit.MILLISECONDS);
                    if (instance == null)
                        throw new PooledException("No instance available.");
                    isAvailable = this.isInstanceAvailable(instance);
                    if (!isAvailable) {
                        LOG.info("Service instance is not available :"
                                + instance);
                    }
                } finally {
                    if (instance != null) {
                        this.instances.offer(instance);
                    }
                }

                ++tries;
                if (tries >= this.instances.size()) {
                    break;
                }
            } while (!isAvailable);

            if (!isAvailable) {
                throw new PooledException("No functional instance available.");
            }
            LOG.trace("Using service instance: " + instance);
        } catch (InterruptedException e) {
            throw new PooledException("Error while get instance.", e);
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see org.jigsaw.payment.rpc.sharder.RefreshableTransportPool#
     * isInstanceAvailable(org.apache.curator.x.discovery.ServiceInstance)
     */
    @Override
    protected boolean isInstanceAvailable(ServiceInstance<RpcPayload> instance) {
        try {
            int failed = this.failedCount.get(instance,
                    this.failedCountCallable).get();
            if (failed >= this.failedMaxCount)
                LOG.trace("Too many failed, failed count is " + failed
                        + ", marked as unavailable :" + instance);
            return failed < this.failedMaxCount;
        } catch (ExecutionException ex) {
            throw new PooledException("Error in get failed account. ", ex);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jigsaw.payment.rpc.sharder.AbstractTransportPool#markError(org
     * .apache.curator.x.discovery.ServiceInstance)
     */
    @Override
    protected void markError(ServiceInstance<RpcPayload> instance) {
        try {
            LOG.info("Marking error on instance: " + instance);
            this.failedCount.get(instance, this.failedCountCallable).addAndGet(
                    1);
        } catch (ExecutionException ex) {
            throw new PooledException("Error in increase failed account. ", ex);
        }
    }

}
