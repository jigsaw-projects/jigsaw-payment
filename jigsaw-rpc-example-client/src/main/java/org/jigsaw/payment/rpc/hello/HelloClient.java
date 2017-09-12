package org.jigsaw.payment.rpc.hello;

import static org.junit.Assert.assertEquals;

import org.jigsaw.payment.Hello;
import org.jigsaw.payment.rpc.sharder.RpcServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月8日
 */
@Component
public class HelloClient {

	@Autowired
	private RpcServiceClient client;
	
	public void callHello() throws Exception {
		String name = "Hello ";
		// System.out.println("start request. ");
		Hello.HelloRequest.Builder request = Hello.HelloRequest.newBuilder();
		request.setName(name);
		Hello.User.Builder user = Hello.User.newBuilder();
		user.setName("hello");
		user.setPassword("hello");
		request.setUser(user.build());
		Hello.HelloResponse response = client.execute("hello", request.build(),
				Hello.HelloResponse.class);
		String message = response.getMessage();
		assertEquals(message, "Hello " + user.getName());
	}

	public void callFoo() throws Exception {
		// System.out.println("start request. ");
		Hello.FooRequest.Builder request = Hello.FooRequest.newBuilder();
		request.setFirst("foo");
		request.setSecond("bar");
		Hello.FooResponse response = client.execute("foo", request.build(),
				Hello.FooResponse.class);
		String message = response.getMessage();
		assertEquals(message, "Foo foo-bar");
	}

	public void run() throws Exception {
		callHello();
		callFoo();
	}

	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				HelloClientConfig.class);
		context.close();
	}

}
