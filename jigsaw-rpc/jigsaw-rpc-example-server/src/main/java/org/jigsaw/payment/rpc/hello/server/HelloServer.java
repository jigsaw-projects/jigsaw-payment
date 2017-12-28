/**
 * 
 */
package org.jigsaw.payment.rpc.hello.server;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月8日
 */
public class HelloServer {
	
	public static void main(String[] args){
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(HelloServerConfig.class);
		context.close();
	}

}
