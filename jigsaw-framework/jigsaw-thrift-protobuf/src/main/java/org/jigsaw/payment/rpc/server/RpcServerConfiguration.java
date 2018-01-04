package org.jigsaw.payment.rpc.server;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Rpc Server 配置文件
 * 
 * @author shamphone@gmail.com
 *
 */

@Configuration
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
public class RpcServerConfiguration {

	@Value("${rpc.server.service.path}")
	private String zkBasePath;

	@Value("${rpc.server.max.qps:1000}")
	private long maxQps = 1000l;

	@Value("${rpc.service.protocol.type:binary}")
	private String protocol = "binary";

	@Value("${rpc.service.transport.type:transport}")
	private String transport = "transport";

	@Value("${rpc.server.zookeeper.username}")
	private String zkUsername;

	@Value("${rpc.server.zookeeper.password}")
	private String zkPassword;

	@Value("${rpc.server.zookeeper.base.sleep.time.ms:1000}")
	private int baseSleepTimeMs = 1000;

	@Value("${rpc.server.zookeeper.max.sleep.time.ms:5000}")
	private int maxSleepTimeMs = 5000;

	@Value("${rpc.server.zookeeper.max.retries:29}")
	private int maxRetries = 29;

	@Value("${rpc.server.zookeeper.connect.string}")
	private String connectString;

	@Value("${rpc.server.zookeeper.session.timeout.ms:1000}")
	private int sessionTimeoutMs = 1000;

	@Value("${rpc.server.zookeeper.connection.timeout.ms:1000}")
	private int connectionTimeoutMs = 1000;

	@Value("${rpc.server.port:7777}")
	private int port = 7777;

	@Value("${rpc.server.ip:#{null}}")
	private String ip = null;

	@Value("${rpc.server.min.worker.threads:512}")
	private int minTheads = 512;

	@Value("${rpc.server.max.worker.threads:3000}")
	private int maxTheads = 3000;

	@Value("${rpc.server.thread.keep.alive.time:600}")
	private long keepAliveTime = 600l;

	@Value("${rpc.server.service.name}")
	private String serviceName = "RPCService";

	@Value("${rpc.server.uri.spec}")
	private String uriSpec = "";
	
	@Value("${rpc.server.zookeeper.register.delay:2000}")
	private int zookeeperDeferRegisterPeriod = 2000;
	
	@Value("${rpc.server.zookeeper.unregister.before:5000}")
	private int zookeeperUnregisterPeriod = 5000;

	

	private RpcPayload payload() {
		RpcPayload payload = new RpcPayload();
		payload.setMaxQps(this.maxQps);
		payload.setProtocol(this.protocol);
		payload.setTransport(this.transport);
		return payload;
	}

	private ACLProvider aclProvider() {
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

	private List<AuthInfo> authInfo() {
		List<AuthInfo> info = new ArrayList<AuthInfo>();
		info.add(new DigestAuthInfo(this.zkUsername, this.zkPassword));
		return info;
	}

	private RetryPolicy retryPolicy() {
		return new BoundedExponentialBackoffRetry(baseSleepTimeMs,
				maxSleepTimeMs, maxRetries);
	}


	/**
	 * 这个bean启动后会独占线程，导致其他的bean无法执行。所以必须保证这个bean在最后才能够执行。
	 * @return
	 * @throws Exception
	 */
	@Bean(initMethod = "start", destroyMethod = "stop")
	public ServerRunner serverRunner()
			throws Exception {
		String ip = this.ip;
		if (ip == null)
			ip = new IpPortResolver().getIpV4Address();

		String instanceId = this.ip + ":" + this.port;
		
		CuratorFramework curatorFramework =CuratorFrameworkFactory.builder()
				.connectString(this.connectString)
				.sessionTimeoutMs(this.sessionTimeoutMs)
				.connectionTimeoutMs(this.connectionTimeoutMs)
				.retryPolicy(this.retryPolicy())
				.aclProvider(this.aclProvider()).authorization(this.authInfo())
				.build();
		InstanceSerializer<RpcPayload> serializer = new JsonSerializer();

		TServerTransport transport = new TServerSocket(this.port);

		TThreadPoolServer.Args args = new TThreadPoolServer.Args(transport);
		args.transportFactory(new TTransportFactory());
		args.protocolFactory(new TBinaryProtocol.Factory());

		TProcessor processor= new TProtobufProcessor();		
		args.processor(processor);
		
		args.executorService(new ThreadPoolExecutor(this.minTheads,
				this.maxTheads, this.keepAliveTime, TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>()));

		TServer server = new TThreadPoolServer(args);

		ServiceInstanceBuilder<RpcPayload> instanceBuilder = ServiceInstance
				.builder();
		instanceBuilder.name(this.serviceName)
				.uriSpec(new UriSpec(this.uriSpec)).payload(this.payload())
				.port(port).id(instanceId).address(ip);

		ServiceDiscoveryBuilder<RpcPayload> discoveryBuilder = ServiceDiscoveryBuilder
				.builder(RpcPayload.class);
		discoveryBuilder.client(curatorFramework).basePath(zkBasePath)
				.serializer(serializer).thisInstance(instanceBuilder.build())
				.build();
		return ServerRunner
				.newBuilder()
				.server(server)
				.curatorFramework(curatorFramework)
				.serviceDiscovery(discoveryBuilder.build())
				.zookeeperDeferRegisterPeriod(this.zookeeperDeferRegisterPeriod)
				.zookeeperUnregisterPeriod(this.zookeeperUnregisterPeriod).build();
	}
}
