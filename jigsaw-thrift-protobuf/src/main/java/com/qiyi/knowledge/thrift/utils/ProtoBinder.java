package com.qiyi.knowledge.thrift.utils;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.protobuf.ProtobufMapper;
import com.qiyi.knowledge.thrift.exception.InvokeException;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lihengjun on 2016/5/11.
 */
public class ProtoBinder {
	private static final Map<Class<Message>, Descriptors.Descriptor> CACHE_MESSAGE_DESCRIPTOR = new HashMap();
	private static ObjectMapper objectMapper = new ProtobufMapper();

	/**
	 * convert object to protobuf message
	 *
	 * @param messageClass
	 * @param paramsObject
	 * @param <T>
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Message> T build(Class<T> messageClass, Object paramsObject) {
		return (T) newBuilder(messageClass, paramsObject).build();
	}

	/**
	 * convert object to protobuf builder
	 *
	 * @param messageClass
	 * @param paramsObject
	 * @param <T>
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static <T extends Message> T.Builder newBuilder(Class<T> messageClass, Object paramsObject) {
		final T.Builder builder = newBuilder(messageClass);
		if (paramsObject == null) {
			return builder;
		}
		final Descriptors.Descriptor descriptor = getDescriptor((Class<Message>) messageClass);
		Map<String, Object> paramsMap = null;
		if (paramsObject instanceof Map) {
			paramsMap = (Map<String, Object>) paramsObject;
		} else {
			try {
				paramsMap = PropertyUtils.describe(paramsObject);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new InvokeException(e);
			}
		}

		return toBuilder(paramsMap, descriptor, builder);
	}

	/**
	 * convert object to protobuf builder
	 *
	 * @param paramsMap
	 * @param descriptor
	 * @param builder
	 * @param <T>
	 * @return
	 */
	private static <T extends Message> T.Builder toBuilder(Map<String, Object> paramsMap,
			Descriptors.Descriptor descriptor, T.Builder builder) {
		if (paramsMap == null || paramsMap.isEmpty()) {
			return builder;
		}

		for(Map.Entry<String,Object> entry:paramsMap.entrySet()){
			Object value = entry.getValue();
			if(value==null)
				continue;
			final Descriptors.FieldDescriptor fieldDescriptor = descriptor.findFieldByName(entry.getKey());

			if (fieldDescriptor != null) {
				if (fieldDescriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
					// when MESSAGE java type, nothing happened
				} else if (fieldDescriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
					// when ENUM java type, nothing happened
				} else {// simple java type,convert and set
					// 转换为相应的类型 ，会自动将 date 类型转换为long
					value = ConvertUtils.convert(value, fieldDescriptor.getDefaultValue().getClass());
					builder.setField(fieldDescriptor, value);
				}
			} else { // find embedded message
				final Object finalValue = value;
				for(Descriptors.FieldDescriptor f:descriptor.getFields()){

					if (f.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
						Message.Builder fBuilder = newBuilder(f.getMessageType());
						toBuilder(paramsMap, f.getMessageType(), fBuilder);
						builder.setField(f, fBuilder.build());
					} else if (f.getName().equals(entry.getKey())) {
						// 转换为相应的类型 ，会自动将 date 类型转换为long
						Object newValue = ConvertUtils.convert(finalValue, f.getDefaultValue().getClass());
						builder.setField(f, newValue);
					}
				}
			}
		}
		return builder;
	}

	/**
	 * get Descriptor from message class cache messageClass->descriptor
	 *
	 * @param messageClass
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	public static Descriptors.Descriptor getDescriptor(Class<Message> messageClass){
		Descriptors.Descriptor descriptor = CACHE_MESSAGE_DESCRIPTOR.get(messageClass);
		if (descriptor == null) {// cache messageClass->descriptor
			try {
				descriptor = (Descriptors.Descriptor) MethodUtils.invokeStaticMethod(messageClass, "getDescriptor");
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				throw new InvokeException(e);
			}// 获取描述
			synchronized (ProtoBinder.class) {
				CACHE_MESSAGE_DESCRIPTOR.put(messageClass, descriptor);
			}
		}
		return descriptor;
	}

	/**
	 * get DynamicMessage.Builder from descriptor
	 *
	 * @param descriptor
	 * @return
	 */
	public static DynamicMessage.Builder newBuilder(Descriptors.Descriptor descriptor) {
		DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
		return builder;
	}

	/**
	 * get Builder from messageClass
	 *
	 * @param messageClass
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	public static <T extends Message> T.Builder newBuilder(Class<T> messageClass) {
		T.Builder builder = null;
		try {
			builder = (T.Builder) MethodUtils.invokeStaticMethod(messageClass, "newBuilder");
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new InvokeException(e);
		}
		return builder;
	}

	/**
	 * get Builder from messageClass
	 *
	 * @param messageClass
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	public static <T extends Message> T parseFrom(Class<T> messageClass, byte[] bytes) {
		T message = null;
		try {
			message = (T) MethodUtils.invokeStaticMethod(messageClass, "parseFrom", bytes);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new InvokeException(e);
		}
		return message;
	}
}
