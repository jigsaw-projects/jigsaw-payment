/**
 * 
 */
package org.jigsaw.payment.rpc.hello.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportFactory;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.jigsaw.payment.rpc.register.DigestAuthInfo;
import org.jigsaw.payment.rpc.register.JsonSerializer;
import org.jigsaw.payment.rpc.register.RpcPayload;
import org.jigsaw.payment.rpc.server.IpPortResolver;
import org.jigsaw.payment.rpc.server.ServerRunner;
import org.jigsaw.payment.rpc.server.TProtobufProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * @author root
 *
 */
@Configuration
@ComponentScan("org.jigsaw.payment.rpc.hello")
@PropertySource("classpath:hello.server.properties")
public class HelloServerConfig {

	@Autowired
	private Environment env;

	@Bean
	public TProcessor processor() {
		return new TProtobufProcessor();
	}

	public InstanceSerializer<RpcPayload> serializer() {
		return new JsonSerializer();
	}

	public RpcPayload payload() {
		RpcPayload payload = new RpcPayload();
		payload.setMaxQps(env.getProperty("rpc.server.max.qps", Long.class,
				1000l));
		payload.setProtocol(env.getProperty("rpc.service.protocol.type",
				"binary"));
		payload.setTransport(env.getProperty("rpc.service.transport.type",
				"transport"));
		return payload;
	}

	public ACLProvider aclProvider() {
		return new ACLProvider() {
			@Override
			public List<ACL> getDefaultAcl() {
				return ZooDefs.Ids.CREATOR_ALL_ACL;
			}

			@Override
			public List<ACL> getAclForPath(String path) {
				return ZooDefs.Ids.CREATOR_ALL_ACL;
			}
		};
	}

	public List<AuthInfo> authInfo() {
		String username = env.getProperty("rpc.server.zookeeper.username");
		String password = env.getProperty("rpc.server.zookeeper.password");
		List<AuthInfo> info = new ArrayList<AuthInfo>();
		info.add(new DigestAuthInfo(username, password));
		return info;
	}

	public RetryPolicy retryPolicy() {
		int baseSleepTimeMs = Integer.parseInt(env.getProperty(
				"rpc.server.zookeeper.base.sleep.time.ms", "1000"));
		int maxSleepTimeMs = Integer.parseInt(env.getProperty(
				"rpc.server.zookeeper.max.sleep.time.ms", "5000"));
		int maxRetries = Integer.parseInt(env.getProperty(
				"rpc.server.zookeeper.max.retries", "29"));
		return new BoundedExponentialBackoffRetry(baseSleepTimeMs,
				maxSleepTimeMs, maxRetries);
	}

	@Bean(name = "curator-framework")
	public CuratorFramework curatorFramework() {
		return CuratorFrameworkFactory
				.builder()
				.connectString(
						env.getProperty("rpc.server.zookeeper.connect.string"))
				.sessionTimeoutMs(
						Integer.parseInt(env.getProperty(
								"rpc.server.zookeeper.session.timeout.ms",
								"10000")))
				.connectionTimeoutMs(
						Integer.parseInt(env.getProperty(
								"rpc.server.zookeeper.connection.timeout.ms",
								"10000"))).retryPolicy(this.retryPolicy())
				.aclProvider(this.aclProvider()).authorization(this.authInfo())
				.build();
	}

	@Bean(name = "service-instance")
	public ServiceInstance<RpcPayload> serviceInstance() throws Exception {
		ServiceInstanceBuilder<RpcPayload> instance = ServiceInstance.builder();
		instance.name(env.getProperty("rpc.server.service.name"))
				.uriSpec(new UriSpec(env.getProperty("rpc.server.uri.spec")))
				.payload(this.payload()).port(this.port())
				.id(this.instanceId()).address(this.ip());
		return instance.build();
	}

	public int port() {
		return env.getProperty("rpc.server.port", Integer.class);
	}

	public String ip() {
		return (env.getProperty("rpc.server.ip") == null) ? new IpPortResolver()
				.getIpV4Address() : env.getProperty("rpc.server.ip");
	}

	public String instanceId() {
		return this.ip() + ":" + this.port();
	}

	@Bean(name = "pool-server")
	public TServer poolServer() throws Exception {
		TServerTransport transport = new TServerSocket(this.port());

		TThreadPoolServer.Args args = new TThreadPoolServer.Args(transport);
		args.transportFactory(new TTransportFactory());
		args.protocolFactory(new TBinaryProtocol.Factory());

		args.processor(this.processor());
		args.executorService(new ThreadPoolExecutor(env.getProperty(
				"rpc.server.min.worker.threads", Integer.class, 512), env
				.getProperty("rpc.server.max.worker.threads", Integer.class,
						65535), env.getProperty(
				"rpc.server.thread.keep.alive.time", Long.class, 600l),
				TimeUnit.SECONDS, new SynchronousQueue<Runnable>()));

		return new TThreadPoolServer(args);
	}

	@Bean(name = "rpc-register")
	public ServiceDiscovery<RpcPayload> serviceDiscovery() throws Exception {
		ServiceDiscoveryBuilder<RpcPayload> builder = ServiceDiscoveryBuilder
				.builder(RpcPayload.class);
		return builder.client(this.curatorFramework())
				.basePath(env.getProperty("rpc.server.service.path"))
				.serializer(this.serializer())
				.thisInstance(this.serviceInstance()).build();
	}

	@Bean(name = "server-runner", initMethod="start", destroyMethod = "stop")
	public ServerRunner serverRunner() throws Exception {
		return ServerRunner
				.newBuilder()
				.server(this.poolServer())
				.curatorFramework(this.curatorFramework())
				.serviceDiscovery(this.serviceDiscovery())
				.zookeeperDeferRegisterPeriod(
						env.getProperty("rpc.server.zookeeper.register.delay",
								Integer.class, 2000))
				.zookeeperUnregisterPeriod(
						env.getProperty(
								"rpc.server.zookeeper.unregister.before",
								Integer.class, 5000)).build();
	}
}
