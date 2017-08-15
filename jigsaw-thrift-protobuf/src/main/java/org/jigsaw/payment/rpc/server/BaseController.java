package org.jigsaw.payment.rpc.server;

import org.jigsaw.payment.rpc.NotFoundException;
import org.jigsaw.payment.rpc.SystemException;
import org.jigsaw.payment.rpc.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;

/**
 * Base Controller, every service must extend this acl.
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 */
public abstract class BaseController<Request extends Message, Response extends Message>
		implements Controller<Request, Response> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
	private static int INTERNAL_ERROR = 502;
	@Override
	public Response process(Request request) throws NotFoundException,SystemException,UserException{
		long beginTime = System.currentTimeMillis();
		try {
			Response response = doProcess(request);
			LOGGER.debug("request {}, process cost time is {}", request.getClass(),
					System.currentTimeMillis() - beginTime);
			return response;
		}catch (NotFoundException|UserException|SystemException|RuntimeException e){
			throw e;
		} catch (Exception ex) {
			LOGGER.error("internal error ", ex);
			throw new SystemException(INTERNAL_ERROR).setMessage(ex.getMessage());
		}
	}

	/**
	 * every service must implement this method
	 * 
	 * @param request
	 * @return response
	 * @throws Exception
	 */
	protected abstract Response doProcess(Request request) throws Exception;
}
