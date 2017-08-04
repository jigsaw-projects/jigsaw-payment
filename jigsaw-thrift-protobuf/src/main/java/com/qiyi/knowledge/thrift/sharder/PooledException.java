/*******************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of Knowlege Tool project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *******************************************************/
package com.qiyi.knowledge.thrift.sharder;

/**
 * pool exception
 * @author Li Hengjun<lihengjun@qiyi.com>
 * @version 1.0.0  5/16/16
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
