/**
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of Knowlege Tool project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 ***/
package com.qiyi.knowledge.thrift.register;

import com.qiyi.knowledge.thrift.utils.JsonBinder;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 *  默认的zk是使用json格式的。
 * 1:序列化时只采用 json;
 * 2:反序列化时使用json格式解析
 *
 * @author Li Hengjun<lihengjun@qiyi.com>
 * @version 1.0.0  5/16/16
 **/
public class JsonSerializer implements InstanceSerializer<RpcPayload> {
    private String defaltTransport;

    @Override
    public byte[] serialize(ServiceInstance<RpcPayload> instance)
            throws Exception {

        Map<String, Object> map = PropertyUtils.describe(instance);
        map.put("uriSpec", this.serializeUriSpec(instance.getUriSpec()));
        map.remove("class");
        String jsonStr = JsonBinder.buildNonNullBinder().toJson(map);

        return jsonStr.getBytes();
    }

    @Override
    public ServiceInstance<RpcPayload> deserialize(byte[] bytes)
            throws Exception {
        ServiceInstanceBuilder<RpcPayload> builder = ServiceInstance.builder();
        String source = new String(bytes);

        Map<String, Object> map = JsonBinder.buildNonNullBinder().fromJson(source, Map.class);

        builder.id(Objects.toString(map.get("id"), ""));
        builder.address(Objects.toString(map.get("address"), null));
        builder.port((Integer)map.get("port"));
        builder.name(Objects.toString(map.get("name"), ""));
        if(map.get("sslPort")!=null) {
            builder.sslPort((Integer) map.get("sslPort"));
        }
        builder.registrationTimeUTC((long)map
                .get("registrationTimeUTC"));
        if(map.get("serviceType")!=null) {
            builder.serviceType(ServiceType.valueOf(Objects.toString(map.get("serviceType"))));
        }

        if (map.containsKey("uriSpec")) {
            builder.uriSpec(this.deserializeUriSpec((String) map.get("uriSpec")));
        }

        if (map.containsKey("payload")) {
            RpcPayload payload = new RpcPayload();
            BeanUtils.populate(payload, (Map<String, Object>) map.get("payload"));
            builder.payload(payload);
        } else if (!StringUtils.isEmpty(defaltTransport)) {
            RpcPayload payload = new RpcPayload();
            payload.setTransport(defaltTransport);
            builder.payload(payload);
        }

        return builder.build();
    }


    private String serializeUriSpec(UriSpec uriSpec) {
        StringBuilder sb = new StringBuilder();
        for (UriSpec.Part part : uriSpec.getParts()) {
            if (part.isVariable()) {
                sb.append("{");
                sb.append(part.getValue());
                sb.append("}");
            } else {
                sb.append(part.getValue());
            }
        }

        return sb.toString();
    }

    private UriSpec deserializeUriSpec(String rawUriSpec) {
        return new UriSpec(rawUriSpec);
    }

    public void setDefaltTransport(String defaltTransport) {
        this.defaltTransport = defaltTransport;
    }
}
