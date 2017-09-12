
package org.jigsaw.payment.user;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动RPC服务器，使用spring boot application
 */
@EnableTransactionManagement
@SpringBootApplication
public class Server{

	/**
	 * 主函数
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Server.class, args);
	}

}
