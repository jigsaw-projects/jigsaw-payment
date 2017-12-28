package org.jigsaw.payment.rpc.register;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
/**
 * 默认的zk是使用json格式的。 1:序列化时只采用 json; 2:反序列化时使用json格式解析
 *
 * @author shamphone@gmail.com
 * @version 1.0.0 5/16/16
 **/
public class JsonSerializer implements InstanceSerializer<RpcPayload> {
	private String defaltTransport;
	private ObjectMapper mapper;

	public JsonSerializer() {
		this.mapper = new ObjectMapper();
		// 设置输出包含的属性
		this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		// 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
		this.mapper.getDeserializationConfig().with(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	@Override
	public byte[] serialize(ServiceInstance<RpcPayload> instance)
			throws Exception {

		Map<String, Object> map = PropertyUtils.describe(instance);
		map.put("uriSpec", this.serializeUriSpec(instance.getUriSpec()));
		map.remove("class");
		String jsonStr = this.toJson(map);

		return jsonStr.getBytes();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ServiceInstance<RpcPayload> deserialize(byte[] bytes)
			throws Exception {
		ServiceInstanceBuilder<RpcPayload> builder = ServiceInstance.builder();
		String source = new String(bytes);

		Map<String, Object> map = this.fromJson(source, Map.class);

		builder.id(Objects.toString(map.get("id"), ""));
		builder.address(Objects.toString(map.get("address"), null));
		builder.port((Integer) map.get("port"));
		builder.name(Objects.toString(map.get("name"), ""));
		if (map.get("sslPort") != null) {
			builder.sslPort((Integer) map.get("sslPort"));
		}
		builder.registrationTimeUTC((long) map.get("registrationTimeUTC"));
		if (map.get("serviceType") != null) {
			builder.serviceType(ServiceType.valueOf(Objects.toString(map
					.get("serviceType"))));
		}

		if (map.containsKey("uriSpec")) {
			builder.uriSpec(this.deserializeUriSpec((String) map.get("uriSpec")));
		}

		if (map.containsKey("payload")) {
			RpcPayload payload = new RpcPayload();
			BeanUtils.populate(payload,
					(Map<String, Object>) map.get("payload"));
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

	/**
	 * 如果JSON字符串为Null或"null"字符串,返回Null. 如果JSON字符串为"[]",返回空集合.	
	 * @throws IOException 
	 */
	private <T> T fromJson(String jsonString, Class<T> clazz) throws IOException {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		return mapper.readValue(jsonString, clazz);

	}

	/**
	 * 如果对象为Null,返回"null". 如果集合为空集合,返回"[]".
	 * 
	 * @throws IOException
	 */
	private String toJson(Object object) throws IOException {

		return mapper.writeValueAsString(object);

	}

	private UriSpec deserializeUriSpec(String rawUriSpec) {
		return new UriSpec(rawUriSpec);
	}

	public void setDefaltTransport(String defaltTransport) {
		this.defaltTransport = defaltTransport;
	}
}
