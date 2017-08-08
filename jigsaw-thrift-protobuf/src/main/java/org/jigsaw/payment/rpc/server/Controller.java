package org.jigsaw.payment.rpc.server;

import org.jigsaw.payment.rpc.NotFoundException;
import org.jigsaw.payment.rpc.SystemException;
import org.jigsaw.payment.rpc.UserException;

import com.google.protobuf.Message;

/**
 * RPC Controller. 各服务应该实现这个接口。
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 **/
public interface Controller<Request extends Message, Response extends Message> {
	/**
	 * 处理请求的接口
	 * @param request 请求
	 * @return 处理结果
	 * @throws NotFoundException 对于读取资源的接口， 如果资源找不到，可以抛出这个异常。
	 * @throws SystemException 服务器端错误
	 * @throws UserException 由于用户原因引起的错误
	 */
	public Response process(Request request) throws NotFoundException,SystemException,UserException;
}
