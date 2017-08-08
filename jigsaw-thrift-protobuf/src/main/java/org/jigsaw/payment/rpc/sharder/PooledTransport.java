package org.jigsaw.payment.rpc.sharder;

import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TTransport;

/**
 * An wrapper on TTransport to support pooling .
 *
 * @author shamphone@gmail.com
 * @version 1.0.0
 **/
public class PooledTransport extends DefaultPooledObject<TTransport> {
    /**
     * @param transport
     */
    public PooledTransport(TTransport transport) {
        super(transport);
    }

}