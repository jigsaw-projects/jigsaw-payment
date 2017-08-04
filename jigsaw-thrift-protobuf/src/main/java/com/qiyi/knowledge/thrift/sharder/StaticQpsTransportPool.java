package com.qiyi.knowledge.thrift.sharder;

import com.qiyi.knowledge.thrift.register.RpcPayload;

import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class StaticQpsTransportPool extends BasicTransportPool {

    private static Logger LOG = LoggerFactory.getLogger(StaticQpsTransportPool.class);

    private static final int BASE_NUM = 1000;

    @Override
    protected void onInstanceAdded(ServiceInstance<RpcPayload> instance) {
        if (!this.instances.contains(instance)) {
            if (instance.getPayload() == null) {
                this.instances.offer(instance);
            } else {
                int count = (int) instance.getPayload().getMaxQps() / BASE_NUM;
                count++;
                LOG.info("Max qps :" + instance.getPayload().getMaxQps()
                        + "  Count" + count);
                for (int i = 0; i < count; i++) {
                    this.instances.offer(instance);
                }
            }
            LOG.info("Add an instance to pool:  " + instance);
            this.failedCount.put(instance, new AtomicInteger(0));
        }
    }

    @Override
    protected void onInstanceRemoved(ServiceInstance<RpcPayload> instance) {

        while (instances.contains(instance)) {
            this.instances.remove(instance);
            LOG.info("Instance removed from pool : " + instance);
        }
        this.failedCount.invalidate(instance);
    }

}
