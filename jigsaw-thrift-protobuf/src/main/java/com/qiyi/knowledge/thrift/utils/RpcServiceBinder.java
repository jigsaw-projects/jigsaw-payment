/**
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 **/

package com.qiyi.knowledge.thrift.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.iqiyi.pay.sdk.RpcServiceMessageOption;
import com.iqiyi.pay.sdk.Taglib;

/**
 * rpc service builder. Init service name,client_class,request entity,response
 * entity
 *
 * @author Li Hengjun<lihengjun@qiyi.com>
 * @version 1.0.0 5/16/16
 **/
public class RpcServiceBinder {
	private final Logger LOGGER = LoggerFactory.getLogger(RpcServiceBinder.class);
	/**
	 * request message class cache
	 */
	private final Map<String, Class<Message>> REQUEST_MESSAGE_CACHE_MAP = new HashMap();
	/**
	 * response message class cache
	 */
	private final Map<String, Class<Message>> RESPONSE_MESSAGE_CACHE_MAP = new HashMap();
	/**
	 * client method name cache
	 */
	private final Map<String, String> METHOD_NAME_CACHE_MAP = new HashMap();
	/**
	 * server service name cache
	 */
	private final Map<String, String> SERVER_SERVICE_NAME_CACHE_MAP = new HashMap();
	/**
	 * client class cache
	 */
	private final Map<String, Class<TServiceClient>> CLIENT_CACHE_MAP = new HashMap();
	/**
	 * request message Descriptor cache
	 */
	private final Map<String, Descriptors.Descriptor> REQUEST_DESCRIPTOR_CACHE_MAP = new HashMap();
	/**
	 * response message Descriptor cache
	 */
	private final Map<String, Descriptors.Descriptor> RESPONSE_DESCRIPTOR_CACHE_MAP = new HashMap();

	public RpcServiceBinder() {
		try {
			init();
		} catch (ClassNotFoundException | IllegalArgumentException e) {
			LOGGER.error("init error,please check proto file config, {}", e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * init system protobuf entity into cach map protobuf entity base package
	 * must be com.iqiyi.pay.sdk
	 *
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	private synchronized void init() throws ClassNotFoundException {
		BeanDefinitionRegistry beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry, false);
		scanner.addIncludeFilter(new AssignableTypeFilter(Message.class));// find message subclass
		Set<BeanDefinition> classes = scanner.findCandidateComponents("com.iqiyi.pay.sdk");// scan base pacakge
		for (BeanDefinition bean : classes) {
			String className = bean.getBeanClassName();
			Class<Message> clazz = (Class<Message>) ClassUtils.getClass(className);
			if (!Message.class.isAssignableFrom(clazz)) {
				continue;
			}
			Descriptors.Descriptor descriptor = ProtoBinder.getDescriptor(clazz);
			// one to many
			List<RpcServiceMessageOption> rpcServiceMessageOptions = descriptor.getOptions()
					.getExtension(Taglib.rpcServiceMessageOption);
			for (RpcServiceMessageOption rpcServiceMessageOption : rpcServiceMessageOptions) {
				if (rpcServiceMessageOption == null) {// rpcServiceMessageOption is null,continue
					continue;
				}
				String serviceName = rpcServiceMessageOption.getName();
				if (StringUtils.isBlank(serviceName)) {// service name is blank,continue;
					continue;
				}
				// request entity
				if (rpcServiceMessageOption.getIsRequest()) {// when it is request entity,get client class name
					REQUEST_MESSAGE_CACHE_MAP.put(serviceName, clazz);
					String clientClassName = rpcServiceMessageOption.getClientClass();// required
					String methodName = rpcServiceMessageOption.getMethodName();// required
					String serverServiceName = rpcServiceMessageOption.getServerServiceName();// required
					if (StringUtils.isBlank(clientClassName)) {
						throw new IllegalArgumentException("clientClass is required in proto file" + className);
					} else if (StringUtils.isBlank(methodName)) {
						throw new IllegalArgumentException("methodName is required in proto file" + className);
					} else if (StringUtils.isBlank(serverServiceName)) {
						throw new IllegalArgumentException("serverServiceName is required in proto file" + className);
					}

					Class clientClass = ClassUtils.getClass(clientClassName);
					if (TServiceClient.class.isAssignableFrom(clientClass)) {
						CLIENT_CACHE_MAP.put(serviceName, clientClass);
					} else {
						throw new IllegalArgumentException(
								"clientClassName must be TServiceClient's subclass,invalid className:" + className);
					}
					METHOD_NAME_CACHE_MAP.put(serviceName, methodName);
					SERVER_SERVICE_NAME_CACHE_MAP.put(serviceName, serverServiceName);
					REQUEST_DESCRIPTOR_CACHE_MAP.put(serviceName, ProtoBinder.getDescriptor(clazz));
				} else {// response entity
					RESPONSE_MESSAGE_CACHE_MAP.put(serviceName, clazz);
					RESPONSE_DESCRIPTOR_CACHE_MAP.put(serviceName, ProtoBinder.getDescriptor(clazz));
				}
			}
		}
	}

	/**
	 * get Request Descriptor by service name
	 *
	 * @param serviceName
	 * @return
	 */
	public Descriptors.Descriptor getRequestDescriptor(String serviceName) {
		return REQUEST_DESCRIPTOR_CACHE_MAP.get(serviceName);
	}

	/**
	 * get Response Descriptor by service name
	 *
	 * @param serviceName
	 * @return
	 */
	public Descriptors.Descriptor getResponseDescriptor(String serviceName) {
		return RESPONSE_DESCRIPTOR_CACHE_MAP.get(serviceName);
	}

	/**
	 * get Request Descriptor by service name
	 *
	 * @param serviceName
	 * @return
	 */
	public Class getClientClass(String serviceName) {
		return CLIENT_CACHE_MAP.get(serviceName);
	}

	/**
	 * get TServiceClient from serviceName and tProtocol
	 *
	 * @param serviceName
	 * @param tProtocol
	 * @return
	 */
	public TServiceClient getClient(String serviceName, TProtocol tProtocol) {
		TServiceClient client = null;
		Class<TServiceClient> clazz = null;
		clazz = getClientClass(serviceName);
		if (clazz == null) {
			new IllegalArgumentException(serviceName + " client class not found");
		}
		try {
			client = ConstructorUtils.invokeConstructor(clazz, tProtocol);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
				| InstantiationException e) {
			new IllegalArgumentException(serviceName + "[" + clazz + "] be found,but constructor newInstance failed ",
					e);
		}
		return client;
	}

	/**
	 * @param serviceName
	 * @return
	 */
	public Class<Message> getRequestClass(String serviceName) {
		return REQUEST_MESSAGE_CACHE_MAP.get(serviceName);
	}

	/**
	 * @param serviceName
	 * @return
	 */
	public Class<Message> getResponseClass(String serviceName) {
		return RESPONSE_MESSAGE_CACHE_MAP.get(serviceName);
	}

	/**
	 * @param serviceName
	 * @param paramsObject
	 * @param <T>
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public <T extends Message> T.Builder newRequestBuilder(String serviceName, Object paramsObject)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<T> requestClass = (Class<T>) REQUEST_MESSAGE_CACHE_MAP.get(serviceName);
		return ProtoBinder.newBuilder(requestClass, paramsObject);
	}

	/**
	 * @param serviceName
	 * @param paramsObject
	 * @param <T>
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public <T extends Message> T newRequestMessage(String serviceName, Object paramsObject)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<T> requestClass = (Class<T>) REQUEST_MESSAGE_CACHE_MAP.get(serviceName);
		return ProtoBinder.build(requestClass, paramsObject);
	}

	/**
	 * @param serviceName
	 * @param paramsObject
	 * @param <T>
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public <T extends Message> T.Builder newResponseBuilder(String serviceName, Object paramsObject)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<T> requestClass = (Class<T>) RESPONSE_MESSAGE_CACHE_MAP.get(serviceName);
		return ProtoBinder.newBuilder(requestClass, paramsObject);
	}

	public Map<String, Class<Message>> getREQUEST_MESSAGE_CACHE_MAP() {
		return REQUEST_MESSAGE_CACHE_MAP;
	}

	public Map<String, Class<Message>> getRESPONSE_MESSAGE_CACHE_MAP() {
		return RESPONSE_MESSAGE_CACHE_MAP;
	}

	public Map<String, String> getMETHOD_NAME_CACHE_MAP() {
		return METHOD_NAME_CACHE_MAP;
	}

	public Map<String, String> getSERVER_SERVICE_NAME_CACHE_MAP() {
		return SERVER_SERVICE_NAME_CACHE_MAP;
	}

	public Map<String, Class<TServiceClient>> getCLIENT_CACHE_MAP() {
		return CLIENT_CACHE_MAP;
	}

	public Map<String, Descriptors.Descriptor> getREQUEST_DESCRIPTOR_CACHE_MAP() {
		return REQUEST_DESCRIPTOR_CACHE_MAP;
	}

	public Map<String, Descriptors.Descriptor> getRESPONSE_DESCRIPTOR_CACHE_MAP() {
		return RESPONSE_DESCRIPTOR_CACHE_MAP;
	}
}
