package org.jigsaw.payment.rpc.sharder;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.CloseableExecutorService;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.jigsaw.payment.rpc.register.JsonSerializer;
import org.jigsaw.payment.rpc.register.RpcPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TProtocol 连接池客户端。 其sharding的策略：
 * <ol>
 * <li>优先选择同网段的服务器</li>
 * <li>优先选择QPS高的服务器</li>
 * </ol>
  * @author shamphone@gmail.com
 * @version 1.0.0  5/16/16
 **/

public abstract class RefreshableTransportPool extends AbstractTransportPool
        implements PathChildrenCacheListener {
    private static Logger LOG = LoggerFactory
            .getLogger(RefreshableTransportPool.class);
    /**
     * this curator client;
     */
    private CuratorFramework client;
    /**
     * path to watch
     */
    private String path;
    /**
     * if true, node contents are cached in addition to the stat
     */
    private boolean cacheData = true;
    /**
     * if true, data in the path is compressed
     */
    private boolean dataIsCompressed = false;
    /**
     * Closeable ExecutorService to use for the PathChildrenCache's background
     * thread
     */
    private CloseableExecutorService executorService = null;

    /**
     * this curator cache.
     */
    private PathChildrenCache cache = null;

    /**
     * available RPC server instance;
     */
    // private Map<String, ServiceInstance<RpcPayload>> instances;

    /**
     * the serializer to de-serialize curator payload.
     */
    private InstanceSerializer<RpcPayload> serializer;

    public RefreshableTransportPool() {
        this.serializer = new JsonSerializer();
    }

    public void setSerializer(InstanceSerializer<RpcPayload> serializer) {
		this.serializer = serializer;
	}

	public void setClient(CuratorFramework client) {
        this.client = client;
    }

    public void setPath(String path) {
        this.path = PathUtils.validatePath(path);
    }

    public void setCacheData(boolean cacheData) {
        this.cacheData = cacheData;
    }

    public void setDataIsCompressed(boolean dataIsCompressed) {
        this.dataIsCompressed = dataIsCompressed;
    }

    public void setCloseableExecutorService(
            CloseableExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = new CloseableExecutorService(executorService);
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.executorService = new CloseableExecutorService(
                Executors.newSingleThreadExecutor(threadFactory), true);
    }

    public void setCache(PathChildrenCache cache) {
        this.cache = cache;
    }

    public void setSocketTimeout(int socketTimeout) {
        super.socketTimeout = socketTimeout;
    }

    public void start() throws Exception {
        if (this.cache == null) {
            if (this.executorService == null) {
                this.cache = new PathChildrenCache(client, path, cacheData);
            } else {
                this.cache = new PathChildrenCache(client, path, cacheData,
                        dataIsCompressed, this.executorService);
            }
        }
        this.cache.getListenable().addListener(this);
        this.cache.start(StartMode.POST_INITIALIZED_EVENT);
        //this.prepareInstances();
        // call super to initialize the pool;
        super.start();
        LOG.info("transport pooling factory started. ");
    }


    @Override
    public void close() throws IOException {
        super.close();
        this.cache.clear();
        this.cache.close();
        LOG.info("transport pooling factory closed. ");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.curator.framework.recipes.cache.PathChildrenCacheListener#
     * childEvent(org.apache.curator.framework.CuratorFramework,
     * org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent)
     */
    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
            throws Exception {
        PathChildrenCacheEvent.Type eventType = event.getType();
        switch (eventType) {
        case INITIALIZED:
            LOG.debug("initailize the service instance list from zookeeper.");
            break;
        case CHILD_ADDED:
            LOG.debug("add new service instance from zookeeper.");
            this.addChild(event.getData());
            break;
        case CHILD_UPDATED:
            LOG.debug("update service instance  from zookeeper.");
            this.addChild(event.getData());
            break;
        case CONNECTION_RECONNECTED:
            this.cache.rebuild();
            break;
        case CHILD_REMOVED:
        case CONNECTION_SUSPENDED:
        case CONNECTION_LOST:
            LOG.debug("remove service instance  from zookeeper.");
            this.removeChild(event.getData());
            break;
        default:
            LOG.debug("Ignore PathChildrenCache event : {path:"
                    + event.getData().getPath() + " data:"
                    + new String(event.getData().getData()) + "}");
        }
    }

    /**
     * add instance;
     *
     * @param data
     */
    private void addChild(ChildData data) {
        if (data == null
                || ArrayUtils.isEmpty(data.getData())) {
            return;
        }

        try {
            ServiceInstance<RpcPayload> instance = serializer.deserialize(data
                    .getData());
            this.onInstanceAdded(instance);
        } catch (Exception ex) {
            LOG.error("Could not add zk node " + data.getPath() + " to pool.",
                    ex);
        }
    }

    /**
     * remove instance;
     *
     * @param data
     */
    private void removeChild(ChildData data) {
        if (data == null) {
            return;
        }

        try {
            ServiceInstance<RpcPayload> instance = serializer.deserialize(data
                    .getData());
            this.onInstanceRemoved(instance);
        } catch (Exception ex) {
            LOG.error("Could not remove zk node " + data.getPath() + " from pool.",
                    ex);
        }
    }


    /**
     * Called when an instance is add / re-add into the group;
     *
     * @param instance
     */
    protected abstract void onInstanceAdded(ServiceInstance<RpcPayload> instance);

    /**
     * Called when an instance is removed from the group;
     *
     * @param instance
     */
    protected abstract void onInstanceRemoved(
            ServiceInstance<RpcPayload> instance);

}
