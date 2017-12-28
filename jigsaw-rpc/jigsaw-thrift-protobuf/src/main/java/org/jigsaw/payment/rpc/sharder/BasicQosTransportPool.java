package org.jigsaw.payment.rpc.sharder;

import org.apache.curator.x.discovery.ServiceInstance;
import org.jigsaw.payment.rpc.register.RpcPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于服务质量的连接池
 * @author shamphone@gmail.com
 * @version 1.0.0
 */
public class BasicQosTransportPool extends RefreshableTransportPool {
    private static Logger LOG = LoggerFactory.getLogger(BasicQosTransportPool.class);

    private Map<ServiceInstance<RpcPayload>, InstanceStatus> qosTransportmap = new ConcurrentHashMap<ServiceInstance<RpcPayload>, InstanceStatus>();

    private Random random = new Random();

    public BasicQosTransportPool() {
    }

    @Override
    public void start() throws Exception {
        super.start();
    }

    @Override
    protected void onInstanceAdded(ServiceInstance<RpcPayload> instance) {

        if (qosTransportmap.get(instance) == null) {
            InstanceStatus transport = new InstanceStatus(instance);
            qosTransportmap.put(instance, transport);
            LOG.info("Add an instance to pool:  " + instance);
        }
    }

    @Override
    protected void onInstanceRemoved(ServiceInstance<RpcPayload> instance) {

        InstanceStatus transport = qosTransportmap.get(instance);
        if (transport != null) {
            qosTransportmap.remove(instance);
            transport.close();
            LOG.info("Instance removed from pool : " + instance);
        }
    }

    /**
     * choose an instance from current instance set;
     * @return
     */
    @Override
    protected ServiceInstance<RpcPayload> chooseInstance() {

        Collection<InstanceStatus> transportList = qosTransportmap.values();
        if (transportList.size() <= 0) {
            throw new PooledException("No instance available.");
        }
        // copy list, to avoid concurrent modification
        List<InstanceStatus> copyList = new ArrayList<InstanceStatus>();
        for (InstanceStatus status : transportList) {
            copyList.add(new InstanceStatus(status.getQuality(), status
                    .isAvailable(), status.getInstance()));
        }
        List<InstanceStatus> availableList = new ArrayList<InstanceStatus>();
        for (InstanceStatus transport : copyList) {
            if (transport.isAvailable()) {
                availableList.add(transport);
            }
        }
        if (availableList.size() <= 0) {
            throw new PooledException("No functional instance available.");
        }
        int totalQuality = 0;
        for (InstanceStatus transport : availableList) {
            totalQuality = totalQuality + transport.getQuality();
        }
        int quality = random.nextInt(totalQuality);
        int low = 0;
        for (int i = 0; i < availableList.size(); i++) {
            InstanceStatus transport = availableList.get(i);
            int high = low + transport.getQuality();
            if (quality >= low && quality < high) {
                return transport.getInstance();
            } else {
                low = low + transport.getQuality();
            }
        }
        // if we don't get instance till now, get the first one
        return availableList.get(0).getInstance();
    }

    /*
     * (non-Javadoc)
     * @see org.jigsaw.payment.rpc.sharder.RefreshableTransportPool#
     * isInstanceAvailable(org.apache.curator.x.discovery.ServiceInstance)
     */
    @Override
    protected boolean isInstanceAvailable(ServiceInstance<RpcPayload> instance) {
        // 在这里不再作判断
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jigsaw.payment.rpc.sharder.AbstractTransportPool#markError(org
     * .apache.curator.x.discovery.ServiceInstance)
     */
    @Override
    protected void markError(ServiceInstance<RpcPayload> instance) {

        LOG.info("Marking error on instance:" + instance);
        InstanceStatus transport = qosTransportmap.get(instance);
        if (transport != null) {
            transport.markError();
        }
    }

    private class InstanceStatus {
        // 增加一个InstanceStatus,用于管理所有的服务器实例（包括服务器器可用的系数）
        private ServiceInstance<RpcPayload> instance;
        private volatile int quality = 10;
        private boolean isAvailable = true;
        private long lastTime = 0;
        private long lastTenSecond = 0;
        private AtomicInteger errorCount = new AtomicInteger(0);
        private AtomicInteger errorLastTenSecond = new AtomicInteger(0);
        // 用于每秒监控的线程池
        private ScheduledExecutorService service;
        private Lock lock = new ReentrantLock();

        public InstanceStatus(int quality, boolean isAvailable,
                ServiceInstance<RpcPayload> instance) {
            this.quality = quality;
            this.isAvailable = isAvailable;
            this.instance = instance;
        }

        public InstanceStatus(ServiceInstance<RpcPayload> instance) {
            this.setInstance(instance);
            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    checkStatus();
                }
            }, 1000, 1001, TimeUnit.MILLISECONDS);
        }

        public void markError() {
            errorCount.incrementAndGet();
            errorLastTenSecond.incrementAndGet();

            if (errorCount.get() > 5 && isAvailable == true) {
                try {
                    lock.lock();
                    if (errorCount.get() > 5 && isAvailable == true) {
                        isAvailable = false;
                        if (quality > 1) {
                            quality--;
                            LOG.info("Downgrade the instance : "
                                    + instance.getAddress()
                                    + ", current quality: " + quality);
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        }

        public void checkStatus() {
            long current = System.currentTimeMillis();
            if (current - lastTime > 1000) {
                lastTime = current;
                isAvailable = true;
                errorCount.set(0);
            }
            if (current - lastTenSecond > 10000) {
                lastTenSecond = current;
                if (errorLastTenSecond.get() == 0 && quality < 10) {
                    try {
                        lock.lock();
                        quality++;
                        LOG.info("Upgrade the instance : "
                                + instance.getAddress() + ", current quality: "
                                + quality);
                    } finally {
                        lock.unlock();
                    }
                }
                errorLastTenSecond.set(0);
            }
        }

        public void close() {
            if (service != null) {
                service.shutdown();
            }
        }

        public ServiceInstance<RpcPayload> getInstance() {
            return instance;
        }

        public void setInstance(ServiceInstance<RpcPayload> instance) {
            this.instance = instance;
        }

        public boolean isAvailable() {
            return isAvailable;
        }

        public int getQuality() {
            return quality;
        }
    }
}
