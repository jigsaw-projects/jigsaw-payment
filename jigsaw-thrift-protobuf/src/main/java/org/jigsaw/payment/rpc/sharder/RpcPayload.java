package org.jigsaw.payment.rpc.sharder;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonRootName;

/**
 * A payload dao for register (thrift) RPC Server to zookeeper. The sharder
 * provider would use this information for choose appropriate server.
 * 
 * @author shamphone@gmail.com
 *
 */
@JsonRootName("rpc")
@JsonAutoDetect
public class RpcPayload implements java.io.Serializable {

	/**
     *
     */
	private static final long serialVersionUID = -1805209922621329565L;
	/**
	 * max qps that could be provided by this server;
	 */
	private long maxQps = 1000l;
	/**
	 * the transport type in used:
	 * <ul>
	 * <li>socket:<code>TSocket</code></li>
	 * <li>framed-transport : <code>TFramedTransport</code></li>
	 * <li>file-transport: <code>TFileTransport</code></li>
	 * <li>non-blocking-socket:<code>TNonblockingTransport</code></li>
	 * <li>http-client:<code>THttpClient</code></li>
	 * </ul>
	 */
	private String transport = "socket";
	/**
	 * the protocol type in used:
	 * <ul>
	 * <li>binary : <code>TBinaryProtocol</code></li>
	 * <li>compact: <code>TCompactProtocol</code></li>
	 * <li>json:<code> TJSONProtocol</code></li>
	 * <li>simple-json:<code> TSimpleJSONProtocol</code></li>
	 * </ul>
	 */
	private String protocol = "binary";

	public long getMaxQps() {
		return maxQps;
	}

	public void setMaxQps(long maxQps) {
		this.maxQps = maxQps;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

}