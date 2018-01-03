package org.jigsaw.payment.user.rpc;

import org.jigsaw.payment.rpc.UserService.GetUserRequest;
import org.jigsaw.payment.rpc.UserService.GetUserResponse;
import org.jigsaw.payment.rpc.server.BaseController;
import org.jigsaw.payment.user.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月9日
 */
@Service("getUser")
public class GetUserController extends
		BaseController<GetUserRequest, GetUserResponse> {
	
	@Autowired
	private AccountRepository userRepository;

	@Override
	protected GetUserResponse doProcess(GetUserRequest request)
			throws Exception {
		if(request.hasTargetUserId())
			this.userRepository.get(request.getTargetUserId());
		return null;
	}

}
