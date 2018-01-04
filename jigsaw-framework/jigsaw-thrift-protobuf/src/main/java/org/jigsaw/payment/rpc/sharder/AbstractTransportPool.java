package org.jigsaw.payment.rpc.sharder;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jigsaw.payment.rpc.register.RpcPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * 实现PooledObjectFactory的基本方法来支持对<code>ObjectPool</code>的实现
 *
 * @author shamphone@gmail.com
 * @version 1.0.0  5/16/16
 **/
public abstract class AbstractTransportPool extends
        BaseKeyedPooledObjectFactory<ServiceInstance<RpcPayload>, TTransport>
        implements Closeable, TransportManager {

    private static Logger LOG = LoggerFactory.getLogger(AbstractTransportPool.class);
    /**
     * 被封装的连接池。key为RPC server 实例，value为链接到该server的transport.
     */
    protected KeyedObjectPool<ServiceInstance<RpcPayload>, TTransport> pool;
    /**
     * 连接池配置
     */
    private GenericKeyedObjectPoolConfig poolConfig;

    /**
     * Ttransport socket超时时间(单位: 毫秒, 默认为3000ms).
     * 即socket connect和read超时时间分别为3000ms.
     */
    protected int socketTimeout = 3000;

    /**
     * 默认的（客户端与单台RPC服务器建立的）空闲连接数
     */
    protected int maxIdlePerKey = 64;

    /**
     * 默认的（客户端与单台RPC服务器建立的）最大连接数
     */
    protected int maxTotalPerKey = 512;

    /**
     * 可管理的Transport，用于在连接池实现中，实现<ol>
     * <li>接管socket/transport的通道管理，不执行实际的关闭。</li>
     * <li>当链接出错时，将链接标记为passive，准备回收。</li>
     * </ol>
     *
     */
    public class ManagedTransport extends TTransportWrapper {
        /**
         * @param transport
         * @param instance
         */
        public ManagedTransport(TTransport transport,
                ServiceInstance<RpcPayload> instance) {
            super(transport, instance);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.jigsaw.payment.rpc.sharder.TTransportWrapper#onException(java
         * .lang.Exception)
         */
        @Override
        public void onException(Exception ex) {
            LOG.warn("Error when r/w data on transport: "
                    + this.getServiceInstance().getAddress() + ":"
                    + this.getServiceInstance().getPort()
                    + ", removing it from pool", ex);
            try {
                pool.invalidateObject(this.getServiceInstance(), this);
                markError(this.getServiceInstance());
            } catch (PooledException e) {
                throw e;
            } catch (Exception e) {
                throw new PooledException("Error in borrow object from pool. ",
                        e);
            }
        }

        /**
         * 关闭socket/transport。在从连接池中摘除链接时调用。
         */
        public void closeInternal() {
            super.close();
        }

        /**
         * 使用方必须调用close方法。无连接池的处理是关闭链接。有连接池的处理是来将transport返回到池中，并不关闭链接。
         */
        @Override
        public void close() {
            if (!isOpen()) {
                return;
            }

            try {
                pool.returnObject(this.getServiceInstance(), this);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new PooledException("Error in return object from pool. ",
                        e);
            }
        }

        /**
         * 在连接池建立链接的时候就打开socket了。
         */
        public void open() throws TTransportException {
        }

    }


    public AbstractTransportPool() {
        this.poolConfig = new GenericKeyedObjectPoolConfig();
        this.poolConfig.setMaxIdlePerKey(maxIdlePerKey);
        this.poolConfig.setMaxTotalPerKey(maxTotalPerKey);
    }

    /**
     *  启动连接池
     *
     * @throws Exception
     */
    public void start() throws Exception {
        LOG.info("Starting transport pool: " + poolConfig);
        this.pool = new GenericKeyedObjectPool<ServiceInstance<RpcPayload>, TTransport>(
                this, this.poolConfig);
    }

    @Override
    public void close() throws IOException {
        LOG.info("Closing transport pool: " + poolConfig);
        try {
            this.pool.clear();
        } catch (IOException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PooledException(ex);
        }
        this.pool.close();

    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.pool2.BasePooledObjectFactory#create()
     */
    @Override
    public TTransport create(ServiceInstance<RpcPayload> instance)
            throws Exception {
        TTransport transport = this.createNativeTransport(instance);
        try {
            transport.open();
        } catch (TException ex) {
            LOG.warn(
                    "Error when creating new transport on server: "
                            + instance.getAddress() + ":" + instance.getPort(),
                    ex);
            markError(instance);
            throw ex;
        }
        return new ManagedTransport(transport, instance);
    }

    /**
     * 获取Transport实例。 <ol>
     * <li>选择一个可用的Instance。选择的方式和实现算法有关。 在子类中提供选择算法。</li>
     * <li>在这个实例的连接池中选择一个可用的链接。</li>
     * </ol>
     */
    @Override
    public TTransport getTransport() throws TException {
        ServiceInstance<RpcPayload> instance = this.chooseInstance();
        if (instance == null)
            return null;
        try {
            return this.pool.borrowObject(instance);
        } catch (TException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new PooledException("Error in borrow object from pool. ", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.commons.pool2.BasePooledObjectFactory#wrap(java.lang.Object)
     */
    @Override
    public PooledObject<TTransport> wrap(TTransport transport) {
        return new PooledTransport(transport);
    }

    /*
     * 确认这个实例上的Transport是否有效。这将在获取实例的时候调用。
     *
     * @see
     * org.apache.commons.pool2.BasePooledObjectFactory#validateObject(org.apache
     * .commons.pool2.PooledObject)
     */
    @Override
    public boolean validateObject(ServiceInstance<RpcPayload> instance,
            PooledObject<TTransport> transport) {
        if (!super.validateObject(instance, transport))
            return false;
        if (!this.isInstanceAvailable(instance))
            return false;
        return ((ManagedTransport) transport.getObject()).isOpen();
    }

    /**
     * 根据rc的设置来确定创建什么类型的transport；
     *
     * @param instance
     * @return
     */
    protected TTransport createNativeTransport(
            ServiceInstance<RpcPayload> instance) {
        TSocket socket = new TSocket(instance.getAddress(), instance.getPort());
        socket.setTimeout(socketTimeout);

        RpcPayload server = instance.getPayload();
        if ((server == null) || (server.getTransport() == null)
                || (server.getTransport().equals("socket"))) {
            return socket;
        } else if ("framed-transport".equals(server.getTransport())) {
            return new TFramedTransport(socket);
        }

        // for default, use TSocket;
        return socket;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.commons.pool2.BasePooledObjectFactory#destroyObject(org.apache
     * .commons.pool2.PooledObject)
     */
    @Override
    public void destroyObject(ServiceInstance<RpcPayload> instance,
            PooledObject<TTransport> transport) throws Exception {
        ((ManagedTransport) transport.getObject()).closeInternal();
        super.destroyObject(instance, transport);
    }

    /**
     * @return
     * @see org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig#getMaxTotal()
     */
    public int getMaxTotal() {
        return poolConfig.getMaxTotal();
    }

    /**
     * @param maxTotal
     * @see org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig#setMaxTotal(int)
     */
    public void setMaxTotal(int maxTotal) {
        poolConfig.setMaxTotal(maxTotal);
    }

    /**
     * @return
     * @see org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig#getMaxTotalPerKey()
     */
    public int getMaxTotalPerKey() {
        return poolConfig.getMaxTotalPerKey();
    }

    /**
     * @param maxTotalPerKey
     * @see org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig#getMaxTotalPerKey()
     */
    public void setMaxTotalPerKey(int maxTotalPerKey) {
        poolConfig.setMaxTotalPerKey(maxTotalPerKey);
    }

    /**
     * @return
     * @see org.apache.commons.pool2.impl.GenericObjectPoolConfig#getMaxIdle()
     */
    public int getMaxIdlePerKey() {
        return poolConfig.getMaxIdlePerKey();
    }

    /**
     * @param maxIdle
     * @see org.apache.commons.pool2.impl.GenericObjectPoolConfig#setMaxIdle(int)
     */
    public void setMaxIdlePerKey(int maxIdle) {
        poolConfig.setMaxIdlePerKey(maxIdle);
    }

    /**
     * @return
     * @see org.apache.commons.pool2.impl.GenericObjectPoolConfig#getMinIdle()
     */
    public int getMinIdlePerKey() {
        return poolConfig.getMinIdlePerKey();
    }

    /**
     * @param minIdle
     * @see org.apache.commons.pool2.impl.GenericObjectPoolConfig#setMinIdle(int)
     */
    public void setMinIdlePerKey(int minIdle) {
        poolConfig.setMinIdlePerKey(minIdle);
    }

    /**
     * @return
     * @see org.apache.commons.pool2.impl.BaseObjectPoolConfig#getLifo()
     */
    public boolean getLifo() {
        return poolConfig.getLifo();
    }

    /**
     * @param lifo
     * @see org.apache.commons.pool2.impl.BaseObjectPoolConfig#setLifo(boolean)
     */
    public void setLifo(boolean lifo) {
        poolConfig.setLifo(lifo);
    }

    /**
     * @return
     * @see org.apache.commons.pool2.impl.BaseObjectPoolConfig#getMaxWaitMillis()
     */
    public long getMaxWaitMillis() {
        return poolConfig.getMaxWaitMillis();
    }

    /**
     * @param maxWaitMillis
     * @see org.apache.commons.pool2.impl.BaseObjectPoolConfig#setMaxWaitMillis(long)
     */
    public void setMaxWaitMillis(long maxWaitMillis) {
        poolConfig.setMaxWaitMillis(maxWaitMillis);
    }

    /**
     * @return
     * @see org.apache.commons.pool2.impl.BaseObjectPoolConfig#getTestOnCreate()
     */
    public boolean getTestOnCreate() {
        return poolConfig.getTestOnCreate();
    }

    /**
     * @param testOnCreate
     * @see org.apache.commons.pool2.impl.BaseObjectPoolConfig#setTestOnCreate(boolean)
     */
    public void setTestOnCreate(boolean testOnCreate) {
        poolConfig.setTestOnCreate(testOnCreate);
    }

    /**
     * @return
     * @see org.apache.commons.pool2.impl.BaseObjectPoolConfig#getTestOnBorrow()
     */
    public boolean getTestOnBorrow() {
        return poolConfig.getTestOnBorrow();
    }

    /**
     * @param testOnBorrow
     * @see org.apache.commons.pool2.impl.BaseObjectPoolConfig#setTestOnBorrow(boolean)
     */
    public void setTestOnBorrow(boolean testOnBorrow) {
        poolConfig.setTestOnBorrow(testOnBorrow);
    }

    /**
     * @return
     * @see org.apache.commons.pool2.impl.BaseObjectPoolConfig#getTestOnReturn()
     */
    public boolean getTestOnReturn() {
        return poolConfig.getTestOnReturn();
    }

    /**
     * @param testOnReturn
     * @see org.apache.commons.pool2.impl.BaseObjectPoolConfig#setTestOnReturn(boolean)
     */
    public void setTestOnReturn(boolean testOnReturn) {
        poolConfig.setTestOnReturn(testOnReturn);
    }

    /**
     * @return
     * @see org.apache.commons.pool2.impl.BaseObjectPoolConfig#getTestWhileIdle()
     */
    public boolean getTestWhileIdle() {
        return poolConfig.getTestWhileIdle();
    }

    /**
     * @param testWhileIdle
     * @see org.apache.commons.pool2.impl.BaseObjectPoolConfig#setTestWhileIdle(boolean)
     */
    public void setTestWhileIdle(boolean testWhileIdle) {
        poolConfig.setTestWhileIdle(testWhileIdle);
    }

    /**
     * 判断一个instance是否可用。子类实现这个方法，根据transport情况来计算。
     *
     * @param instance
     */
    protected abstract boolean isInstanceAvailable(
            ServiceInstance<RpcPayload> instance);

    /**
     * 由子类实现的选择算法
     *
     * @return
     */
    protected abstract ServiceInstance<RpcPayload> chooseInstance();

    /**
     * 如果出现传输错误，由子类如何处理该instance。
     *
     * @param instance
     */
    protected abstract void markError(ServiceInstance<RpcPayload> instance);
}
