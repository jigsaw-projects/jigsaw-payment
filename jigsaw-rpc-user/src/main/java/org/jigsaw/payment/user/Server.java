
package org.jigsaw.payment.user;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * shard server
 */
@EnableTransactionManagement
@SpringBootApplication
public class Server{

	/**
	 * main function
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Server.class, args);
	}

}
