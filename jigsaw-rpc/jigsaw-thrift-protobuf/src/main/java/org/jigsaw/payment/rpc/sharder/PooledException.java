package org.jigsaw.payment.rpc.sharder;

/**
 * 连接池异常
 * @author shamphone@gmail.com
 * @version 1.0.0
 **/
public class PooledException extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = -5282079758991156441L;

    public PooledException() {
        super();
    }

    public PooledException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PooledException(String message, Throwable cause) {
        super(message, cause);
    }

    public PooledException(String message) {
        super(message);
    }

    public PooledException(Throwable cause) {
        super(cause);
    }

}
