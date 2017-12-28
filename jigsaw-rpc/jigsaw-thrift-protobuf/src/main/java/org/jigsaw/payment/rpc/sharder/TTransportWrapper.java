package org.jigsaw.payment.rpc.sharder;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jigsaw.payment.rpc.register.RpcPayload;


/**
 * A wrapper on TTransport to override its close method and error handling. when
 * closed, it will be return to the pool. It will be marked as invalid when
 * exception happened in transfering.
 *
 * @author shamphone@gmail.com
 * @version 1.0.0
 **/
public abstract class TTransportWrapper extends TTransport {
    private TTransport transport;
    private ServiceInstance<RpcPayload> instance;

    public TTransportWrapper(TTransport transport,
            ServiceInstance<RpcPayload> instance) {
        this.transport = transport;
        this.instance = instance;
    }

    @Override
    public void consumeBuffer(int len) {
        transport.consumeBuffer(len);
    }

    @Override
    public boolean equals(Object obj) {
        return transport.equals(obj);
    }

    @Override
    public boolean peek() {
        return transport.peek();
    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        try {
            return transport.read(buf, off, len);
        } catch (TTransportException ex) {
            this.onException(ex);
            throw ex;
        } catch (RuntimeException re) {
            this.onException(re);
            throw re;
        }
    }

    @Override
    public int readAll(byte[] buf, int off, int len) throws TTransportException {
        try {
            return transport.readAll(buf, off, len);
        } catch (TTransportException ex) {
            this.onException(ex);
            throw ex;
        } catch (RuntimeException re) {
            this.onException(re);
            throw re;
        }
    }

    @Override
    public void write(byte[] buf) throws TTransportException {
        try {
            transport.write(buf);
        } catch (TTransportException ex) {
            this.onException(ex);
            throw ex;
        } catch (RuntimeException ex) {
            this.onException(ex);
            throw ex;
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {
        try {
            transport.write(buf, off, len);
        } catch (TTransportException ex) {
            this.onException(ex);
            throw ex;
        } catch (RuntimeException ex) {
            this.onException(ex);
            throw ex;
        }
    }

    @Override
    public void flush() throws TTransportException {
        try {
            transport.flush();
        } catch (TTransportException ex) {
            this.onException(ex);
            throw ex;
        } catch (RuntimeException ex) {
            this.onException(ex);
            throw ex;
        }
    }

    @Override
    public byte[] getBuffer() {
        return transport.getBuffer();
    }

    @Override
    public int getBufferPosition() {
        return transport.getBufferPosition();
    }

    @Override
    public int getBytesRemainingInBuffer() {
        return transport.getBytesRemainingInBuffer();
    }

    @Override
    public int hashCode() {
        return transport.hashCode();
    }

    @Override
    public boolean isOpen() {
        return transport.isOpen();
    }

    /**
     * do nothing; it will be open in pooling with openInternal;
     */
    @Override
    public void open() throws TTransportException {
        transport.open();
    }

    /* (non-Javadoc)
     * @see org.apache.thrift.transport.TTransport#close()
     */
    @Override
    public void close() {
        this.transport.close();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{'address':").append(this.instance.getAddress())
                .append("{'instance-id':").append(this.instance.getId())
                .append("{'registration-time':")
                .append(this.instance.getRegistrationTimeUTC())
                .append("{'instance-name':").append(this.instance.getName())
                .append("}");
        return buffer.toString();
    }

    /**
     * inner process exception.
     *
     * @param ex
     */
    public abstract void onException(Exception ex) ;

    /**
     * the service instance.
     *
     * @return
     */
    public ServiceInstance<RpcPayload> getServiceInstance() {
        return this.instance;
    }
}
