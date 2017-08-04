package com.qiyi.knowledge.thrift.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * base server
 */

public class BaseRPCServer {
	protected final Logger LOG =  LoggerFactory.getLogger(this.getClass());
	/**
	 * 启动server
	 */
	public void start(String[] args) throws Exception {
		SpringApplication springApplication = new SpringApplication(this.getClass());
		//not web app
		//normally spring select context based on classpath, so we don't need set this is pom is clear
		//springApplication.setWebEnvironment(false);
		ConfigurableApplicationContext context = springApplication.run(args);

		ServerRunner serverRunner = context.getBean("server-runner",ServerRunner.class);

		LOG.info("Started Server successfully on port [{}]",context.getBean(Environment.class).getProperty("rpc.server.port"));
		serverRunner.start();
	}


}
