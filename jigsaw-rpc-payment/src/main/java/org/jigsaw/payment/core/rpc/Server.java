
package org.jigsaw.payment.core.rpc;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * shard server
 */
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
