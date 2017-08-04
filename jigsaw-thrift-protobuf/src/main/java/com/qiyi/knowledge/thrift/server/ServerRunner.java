/**
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 **/
package com.qiyi.knowledge.thrift.server;

import java.util.concurrent.TimeUnit;

import com.qiyi.knowledge.thrift.register.RpcPayload;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.log4j.Logger;
import org.apache.thrift.server.TServer;


/**
 *
 *
 * @author Li Hengjun<lihengjun@qiyi.com>
 * @version 1.0.0  5/16/16
 **/
public class ServerRunner {
	private static final Logger LOG = Logger.getLogger(ServerRunner.class);

	public static class Builder {
		// 在server完全启动后注册到zookeeper上；
		private int zookeeperDeferRegisterPeriod = 2000;
		// 在server关闭提前从zookeeper上注销
		private int zookeeperUnregisterPeriod = 5000;

		private CuratorFramework framework = null;
		private TServer server = null;
		private ServiceDiscovery<RpcPayload> discovery = null;

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
		LOG.info("rpc server start serving... ");
		builder.server.serve();
	}

	public void stop() throws InterruptedException {
		LOG.info("stopping the thrift server...");
		try {
			if (registered) {
				builder.discovery.close();
				builder.framework.close();
			}
			registered = false;
			LOG.info("unregister from zookeeper server. ");
			TimeUnit.MILLISECONDS.sleep(builder.zookeeperUnregisterPeriod);
		} catch (Exception e) {
			LOG.error("Failed to unregister from zookeeper server.", e);
		}
		if(builder.server.isServing())
			builder.server.stop();
		LOG.info("rpc server stoped. ");
	}
}
