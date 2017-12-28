package org.jigsaw.payment.rpc.hello.server;

import org.jigsaw.payment.Hello;
import org.jigsaw.payment.Hello.HelloRequest;
import org.jigsaw.payment.Hello.HelloResponse;
import org.jigsaw.payment.rpc.NotFoundException;
import org.jigsaw.payment.rpc.SystemException;
import org.jigsaw.payment.rpc.UserException;
import org.jigsaw.payment.rpc.server.Controller;
import org.springframework.stereotype.Component;
/**
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月8日
 */
@Component("hello")
public class HelloController  implements Controller<Hello.HelloRequest, Hello.HelloResponse>{


	@Override
	public HelloResponse process(HelloRequest request) throws NotFoundException,SystemException,UserException {
		HelloResponse.Builder builder = HelloResponse.newBuilder();
		builder.setMessage("Hello " + request.getUser().getName());
		return builder.build();
	}


}
