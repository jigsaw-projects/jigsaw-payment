/**
 * 
 */
package org.jigsaw.payment.rpc.hello.server;

import org.jigsaw.payment.Hello;
import org.jigsaw.payment.rpc.NotFoundException;
import org.jigsaw.payment.rpc.SystemException;
import org.jigsaw.payment.rpc.UserException;
import org.jigsaw.payment.rpc.server.Controller;
import org.springframework.stereotype.Component;

/**
 * @author root
 *
 */
@Component("foo")
public class FooController implements
		Controller<Hello.FooRequest, Hello.FooResponse> {

	@Override
	public Hello.FooResponse process(Hello.FooRequest request)
			throws NotFoundException,SystemException,UserException {
		Hello.FooResponse.Builder response = Hello.FooResponse.newBuilder();
		response.setMessage("Foo " + request.getFirst() + "-"
				+ request.getSecond());
		return response.build();
	}

}
