package org.jigsaw.payment.rpc.sharder;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

/**
 * A factory for connections to the thrift server (cluster)  that this TransportManager object represents.
 * The TransportManager interface is implemented by a driver provider. There are three types of implementations: <ol>
 * <li>Basic implementation -- produces a standard TTransport object </li>
 * <li> Connection pooling implementation -- produces a TTransport object that will automatically participate in connection pooling.
 * This implementation works with a middle-tier connection pooling manager.
 * <li>Distributed transaction implementation -- produces a TTransport object that may be used for distributed transactions and almost always participates in connection pooling.
 * This implementation works with a middle-tier transaction manager and almost always with a connection pooling manager. </li>
 * </ol>
A TransportManager object has properties that can be modified when necessary.
For example, if the transport manager is moved to a different server, the property for the server can be changed.
The benefit is that because the transport manager's properties can be changed, any code accessing that transport manager does not need to be changed.

 * @author shamphone@gmail.com
 * @version 1.0.0
 **/
public interface TransportManager {
    /**
     * 获取一个transport
     * @return
     * @throws TException
     */
    public TTransport getTransport() throws TException;
}
