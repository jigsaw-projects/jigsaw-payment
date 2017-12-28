
package org.jigsaw.payment.user;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动RPC服务器，使用spring boot application。 
 */
@EnableTransactionManagement
@SpringBootApplication
public class Server{

	/**
	 * 通过SpringApplication来启动RpCServer。 RPCServer配置在Jigsaw-thrift-protobuf项目中。
	 * @param args 参考Spring boot 的启动参数说明。
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Server.class, args);
	}

}
