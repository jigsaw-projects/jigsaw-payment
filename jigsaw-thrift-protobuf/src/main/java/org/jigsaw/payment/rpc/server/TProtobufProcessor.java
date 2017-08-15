package org.jigsaw.payment.rpc.server;

import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TType;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.jigsaw.payment.rpc.BaseService;
import org.jigsaw.payment.rpc.BaseService.execute_result;
import org.jigsaw.payment.rpc.BaseService.execute_result._Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.GenericTypeResolver;

import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;

/**
 * protobuf和thrift 之间的链接。 实现如下功能：
 * <ol>
 * <li>根据请求的service name，选择对应的<code>Controller</code>来执行请求</li>
 * <li>将请求的byte buffer 转换成对应的Message对象。这个需要依赖Controller的Request和Response的类型。</li>
 * </ol>
 * <p>
 * <p>
 * 当controller处理出错时,增加往客户端显示异常信息(即将异常信息写回给client端）
 *
 * @author shamphone@gmail.com
 * @version 1.0.0
 **/
public class TProtobufProcessor implements org.apache.thrift.TProcessor,
		BeanFactoryAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(TProtobufProcessor.class);

	private static final int MAX_REQUEST_BYTES_LENGTH = 2048;
	private static final String UN_KNOWN_IP = "unknown_ip";

	private BeanFactory beanFactory;
	private JsonFormat format = new JsonFormat();
	
	public TProtobufProcessor() {
	}

	@Override
	public boolean process(TProtocol in, TProtocol out) throws TException {
		TMessage msg = in.readMessageBegin();
		Controller<?, ?> fn = (Controller<?, ?>) this.beanFactory
				.getBean(msg.name);
		if (fn == null) {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("Invalid request: failed to find interface="
						+ msg.name + ", from: " + getInetAddress(in));
			}

			TProtocolUtil.skip(in, TType.STRUCT);
			in.readMessageEnd();
			TApplicationException x = new TApplicationException(
					TApplicationException.UNKNOWN_METHOD,
					"Invalid method name: '" + msg.name + "'");
			out.writeMessageBegin(new TMessage(msg.name,
					TMessageType.EXCEPTION, msg.seqid));
			x.write(out);
			out.writeMessageEnd();
			out.getTransport().flush();
			return true;
		}
		process(msg.seqid, msg.name, in, out, fn);
		return true;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	protected void process(int seqid, String methodName, TProtocol iprot,
			TProtocol oprot, Controller<?, ?> iface) throws TException {
		long startTime = System.currentTimeMillis();
		String ip = getInetAddress(iprot);

		BaseService.execute_args args = new BaseService.execute_args();
		try {
			args.read(iprot);
		} catch (TProtocolException e) {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("Invalid request: failed to parse request of interface="
						+ methodName + ", from: " + ip);
			}

			iprot.readMessageEnd();
			TApplicationException x = new TApplicationException(
					TApplicationException.PROTOCOL_ERROR, e.getMessage());
			oprot.writeMessageBegin(new TMessage(methodName,
					TMessageType.EXCEPTION, seqid));
			x.write(oprot);
			oprot.writeMessageEnd();
			oprot.getTransport().flush();
			return;
		}
		iprot.readMessageEnd();
		TBase<?, ?> result = null;
		try {
			result = getResult(iface, args.request, methodName, ip, oprot,
					seqid);
		} catch (TException tex) {
			LOGGER.error("Internal error processing " + methodName, tex);
			TApplicationException x = new TApplicationException(
					TApplicationException.INTERNAL_ERROR, new StringBuilder()
							.append("Internal error processing ")
							.append(methodName).append("[")
							.append(tex.getClass().getSimpleName()).append("]")
							.append(", cause by: ").append(tex.getMessage())
							.toString());
			oprot.writeMessageBegin(new TMessage(methodName,
					TMessageType.EXCEPTION, seqid));
			x.write(oprot);
			oprot.writeMessageEnd();
			oprot.getTransport().flush();
			return;
		}

		if (!isOneway()) {
			oprot.writeMessageBegin(new TMessage(methodName,
					TMessageType.REPLY, seqid));
			result.write(oprot);
			oprot.writeMessageEnd();
			oprot.getTransport().flush();
		}

		if (LOGGER.isDebugEnabled()) {
			long elapseTime = System.currentTimeMillis() - startTime;
			LOGGER.debug(new StringBuilder()
					.append("End handling query interface=").append(methodName)
					.append(", time elapsed: ").append(elapseTime).toString());
		}
	}

	protected boolean isOneway() {
		return false;
	}

	public <Request extends Message, Response extends Message> TBase<?, ?> getResult(
			Controller<Request, Response> iface, ByteBuffer args,
			String methodName, String ip, TProtocol oprot, int seqid)
			throws TException {
		BaseService.execute_result result = new BaseService.execute_result();
		Request request = this.parseRequest(iface, args);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(new StringBuilder()
					.append("Begin handling query interface=")
					.append(methodName).append(", request: ")
					.append(messageToString(request)).append(", from: ")
					.append(ip).toString());
		}

		Response response = null;
		try {
			response = iface.process(request);
		} catch (TException e) {
			if (this.handleException(result, e)) {// 处理异常成功,直接返回
				return result;
			} else {
				throw e;
			}
		}
		result.success = ByteBuffer.wrap(response.toByteArray());
		return result;
	}

	@SuppressWarnings("unchecked")
	protected <T extends Message> T parseRequest(Controller<?, ?> controller,
			ByteBuffer request) throws TException {
		Class<?> requestClass = this.getParameterizedType(controller);
		try {
			Method method = requestClass.getMethod("parseFrom", byte[].class);
			return (T) method.invoke(null, this.getValidBytes(request));
		} catch (Exception ex) {
			throw new TException("Error in parsing request. ", ex);
		}

	}

	private byte[] getValidBytes(ByteBuffer byteBuffer) {
		return ArrayUtils.subarray(byteBuffer.array(), byteBuffer.position(),
				byteBuffer.limit());
	}

	private Class<?> getParameterizedType(Object target) throws TException {
		Class<?>[] arguments = GenericTypeResolver.resolveTypeArguments(
				target.getClass(), Controller.class);
		if (arguments.length != 2)
			throw new TException(
					"Error to resolve request type, please make sure "
							+ target.getClass()
							+ " has provided type arguments for Controller class.");
		return arguments[0];
	}

	private String getInetAddress(TProtocol in) {
		TTransport transport = in.getTransport();
		if (transport != null && transport instanceof TSocket) {
			Socket socket = ((TSocket) in.getTransport()).getSocket();
			return socket.getInetAddress().getHostAddress().replace('.', ':');
		} else {
			return UN_KNOWN_IP;
		}
	}

	/**
	 * 将message转换成string,并用空格取代回车符和引号.
	 */
	private String messageToString(Message message) {
		String json = format.printToString(message);

		return StringUtils.replaceChars(StringUtils.replaceChars(
				StringUtils.left(json, MAX_REQUEST_BYTES_LENGTH), '\n', ' '),
				'"', ' ');
	}

	/**
	 * 处理抛出异常
	 * 
	 * @param result
	 *            结果
	 * @param e
	 *            异常
	 * @return 是否处理成功
	 */
	private boolean handleException(execute_result result, TException e) {
		String simpleName = e.getClass().getSimpleName();
		simpleName = simpleName.substring(0, 1).toLowerCase()
				+ simpleName.substring(1);
		final _Fields fields = _Fields.findByName(simpleName);
		if (fields == null) {// 没找到对应异常,处理失败
			return false;
		}
		result.setFieldValue(fields, e);
		return true;
	}
}
