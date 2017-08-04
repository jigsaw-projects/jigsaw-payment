/**
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 **/

package com.qiyi.knowledge.thrift.sharder;

import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TTransport;

/**
 * An wrapper on TTransport to support pooling .
 *
 * @author Li Hengjun<lihengjun@qiyi.com>
 * @version 1.0.0  5/16/16
 **/
public class PooledTransport extends DefaultPooledObject<TTransport> {
    /**
     * @param transport
     */
    public PooledTransport(TTransport transport) {
        super(transport);
    }

}