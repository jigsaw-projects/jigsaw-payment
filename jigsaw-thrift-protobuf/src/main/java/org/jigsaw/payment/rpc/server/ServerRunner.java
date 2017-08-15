package org.jigsaw.payment.rpc.server;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.thrift.server.TServer;
import org.jigsaw.payment.rpc.register.RpcPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 */
public class ServerRunner {
	private static final Logger LOG = LoggerFactory.getLogger(ServerRunner.class);

	public static class Builder {
		// 在server完全启动后注册到zookeeper上的时间延迟
		private int zookeeperDeferRegisterPeriod = 2000;
		// 在server关闭提前从zookeeper上注销的时间间隔
		private int zookeeperUnregisterPeriod = 5000;
		// 连接到zookeeper
		private CuratorFramework framework = null;
		// 运行的服务器；
		private TServer server = null;
		// zk服务发现设置
		private ServiceDiscovery<RpcPayload> discovery = null;
		// 是否在子线程中启动server
		private boolean async = false;
		// 异步线程名称
		private String threadName = "RPCService";
		// 异步线程关闭timeout;
		private int shutdownTimeout = 1000;
		/**
		 * 在server完全启动后注册到zookeeper上的时间间隔，毫秒为单位
		 * 
		 * @param milliseconds
		 * @return
		 */
		public Builder zookeeperDeferRegisterPeriod(int milliseconds) {
			this.zookeeperDeferRegisterPeriod = milliseconds;
			return this;
		}

		/**
		 * 在server关闭提前从zookeeper上注销
		 * 
		 * @param milliseconds
		 * @return
		 */
		public Builder zookeeperUnregisterPeriod(int milliseconds) {
			this.zookeeperUnregisterPeriod = milliseconds;
			return this;
		}

		public Builder curatorFramework(CuratorFramework framework) {
			this.framework = framework;
			return this;
		}

		public Builder serviceDiscovery(ServiceDiscovery<RpcPayload> discovery) {
			this.discovery = discovery;
			return this;
		}

		public Builder server(TServer server) {
			this.server = server;
			return this;
		}

		public Builder async(boolean async) {
			this.async = async;
			return this;
		}

		public Builder threadName(String threadName) {
			this.threadName = threadName;
			return this;
		}
		public Builder shutdownTimeout(int shutdownTimeout) {
			this.shutdownTimeout = shutdownTimeout;
			return this;
		}
		public ServerRunner build() {
			return new ServerRunner(this);
		}
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	private Builder builder;
	// 是否注册到zk上了；
	private boolean registered = false;
	// 异步线程；
	private Thread runningThread = null;

	private ServerRunner(Builder builder) {
		this.builder = builder;
	}

	/**
	 * 启动rpc服务器并注册到zookeeper上
	 */
	public void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TimeUnit.MILLISECONDS
							.sleep(builder.zookeeperDeferRegisterPeriod);

				} catch (InterruptedException e) {
					return;

				}
				try {
					builder.framework.start();
					builder.discovery.start();
					registered = true;
				} catch (Exception ex) {
					LOG.error("Failed to start the zookeeper register.", ex);
					return;
				}
				LOG.info("Register to zookeeper successfully.");	
			}
		}).start();	
		if(builder.async){			
			Runnable runnable = new Runnable(){
				@Override
				public void run() {
					LOG.info("rpc server start serving... ");
					builder.server.serve();					
				}
			};
			this.runningThread = new Thread(runnable, builder.threadName);
			this.runningThread.start();		
			
		}else {
			LOG.info("rpc server start serving... ");
			builder.server.serve();
		}	
	}

	public void stop() throws InterruptedException {
		LOG.info("stopping the thrift server...");
		try {
			if (registered) {
				builder.discovery.close();
				LOG.info("Unregister from zookeeper server successfully. ");
				builder.framework.close();
				LOG.info("Close zookeeper connection successfully. ");
			}
			registered = false;
			
			TimeUnit.MILLISECONDS.sleep(builder.zookeeperUnregisterPeriod);
		} catch (Exception e) {
			LOG.error("Failed to unregister from zookeeper server.", e);
		}
		if (builder.server.isServing()){
			builder.server.stop();
			LOG.info("Rpc server shut down.");
		}
		if(builder.async && this.runningThread!=null && this.runningThread.isAlive()){
			this.runningThread.join(builder.shutdownTimeout);
			LOG.info("Rpc server thread "+ this.builder.threadName +" shut down.");
		}
		LOG.info("rpc server stopped successfully. ");
	}
}
