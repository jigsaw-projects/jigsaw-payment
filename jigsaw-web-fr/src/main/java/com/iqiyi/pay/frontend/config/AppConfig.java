/**
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 **/

package com.iqiyi.pay.frontend.config;

import com.iqiyi.pay.client.AccountService;
import com.iqiyi.pay.client.ConfigService;
import com.iqiyi.pay.client.EntityService;
import com.iqiyi.pay.client.PassportService;
import com.iqiyi.pay.client.RouteService;
import com.iqiyi.pay.client.TradeService;
import com.iqiyi.pay.client.TradingService;
import com.iqiyi.pay.monitor.reporter.ganglia.GangliaReporter;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.squareup.okhttp.logging.HttpLoggingInterceptor.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.OkHttpClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * configuration
 *
 * @author Li Hengjun<lihengjun@qiyi.com>
 * @version 1.0.0  7/4/16
 **/
@Configuration
@EnableAsync
@ComponentScan({"com.iqiyi.pay.web.spt", "com.iqiyi.pay.frontend"})
public class AppConfig  extends WebMvcConfigurerAdapter {


    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${spring.redis.password}")
    private String password;

    @Value("${okhttp.connectionPool.aliveTime}")
    private int aliveTime;



    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setContextPath("/pay-web-frontend");
        return factory;
    }

    @Bean
    public String profileValue(Environment environment){
        return environment.getActiveProfiles()[0];
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        return taskExecutor;
    }


    @Bean
    public TradeService tradeService(String profileValue){
        TradeService tradeService = TradeService.newBuilder().activeEnv(profileValue).username("pay-gateway").password("gateway@pay.Qiyi").qps(10).build();
        return tradeService;
    }


    @Bean
    public TradingService tradingService(String profileValue){
        TradingService tradingService = TradingService.newBuilder().activeEnv(profileValue).username("pay-gateway").password("gateway@pay.Qiyi").qps(10).build();
        return tradingService;
    }

    @Bean
    public RouteService routeService(String profileValue){
        RouteService routeService = RouteService.newBuilder().activeEnv(profileValue).username("pay-gateway").password("gateway@pay.Qiyi").qps(10).build();
        return routeService;
    }


    @Bean
    public PassportService passportService(String profileValue){
        PassportService passportService = PassportService.newBuilder().activeEnv(profileValue).username("pay-gateway").password("gateway@pay.Qiyi").qps(10).build();
        return passportService;
    }


    @Bean
    public EntityService entityService(String profileValue){
        EntityService entityService = EntityService.newBuilder().activeEnv(profileValue).username("pay-entity").password("qiyipay").qps(10).build();
        return entityService;
    }


    @Bean
    public AccountService accountService(String profileValue){
        AccountService accountService =AccountService.newBuilder()
                .activeEnv(profileValue).qps(10)
                .retryCount(1)
                .username("pay-account")
                .password("qiyipay")
                .build();
        return accountService;
    }

    @Bean
    public ConfigService configService(String profileValue){
        ConfigService configService = ConfigService.newBuilder().activeEnv(profileValue).username("pay-gateway").password("gateway@pay.Qiyi").qps(10).build();
        return configService;
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        return stringRedisSerializer;
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(host);
        factory.setPort(port);
        factory.setTimeout(timeout);
        factory.setPassword(password);
        return factory;
    }

    @Bean(name="stringRedisTemplate")
    public StringRedisTemplate redisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    @Bean(name="intRedisTemplate")
    public RedisTemplate<String, Integer> intRedisTemplate() {
        RedisTemplate<String, Integer> template = new RedisTemplate<String, Integer>();
        template.setValueSerializer(new GenericToStringSerializer<Integer>(Integer.class));
        template.setKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    @Bean(name="mapRedisTemplate")
    public RedisTemplate<String, Map<String, Object>> mapRedisTemplate() {
        RedisTemplate<String, Map<String, Object>> template = new RedisTemplate<String, Map<String, Object>>();
        template.setValueSerializer(new Jackson2JsonRedisSerializer<Map>(Map.class));
        template.setKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

	@Bean 
	public RestTemplate getRestTemplate() {
		
		OkHttpClient client = new OkHttpClient();
		HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
		logger.setLevel(Level.BODY);
		client.interceptors().add(logger);
		OkHttpClientHttpRequestFactory factory = new OkHttpClientHttpRequestFactory(client);
		
		factory.setConnectTimeout(5000);
		factory.setReadTimeout(3000);
		factory.setWriteTimeout(1000);
		
		RestTemplate t = new RestTemplate();
		t.setRequestFactory(factory);
		
		t.getMessageConverters().add(new ByteArrayHttpMessageConverter());
		return t;
	}



    @Bean(name = "payApiRefundOkHttpClient")
    public OkHttpClient payApiRefundOkHttpClient(@Value("${okhttp.apiRefund.connectTimeout}") int connectTimeout,
                                               @Value("${okhttp.apiRefund.readTimeout}") int readTimeout,
                                               @Value("${okhttp.apiRefund.poolsize}") int connections){
        return buildOkHttpClient(connectTimeout, readTimeout, connections);
    }


    private OkHttpClient buildOkHttpClient(int connectTimeout, int timeout, int connections){
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setFollowRedirects(false);
        okHttpClient.setFollowSslRedirects(false);
        okHttpClient.setConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(timeout, TimeUnit.MILLISECONDS);
        ConnectionPool connectionPool = new ConnectionPool(connections, aliveTime, TimeUnit.SECONDS);
        okHttpClient.setConnectionPool(connectionPool);
        return okHttpClient;
    }


    @Value("${ganglia.monitor.host}")
    private String gangliaHost;
    @Value("${ganglia.monitor.port}")
    private String gangliaPort;
    @Value("${ganglia.monitor.group}")
    private String gangliaGroup;

    @Bean(initMethod = "init")
    public GangliaReporter gangliaReporter(){
        GangliaReporter gangliaReporter = new GangliaReporter();
        gangliaReporter.setHost(gangliaHost);
        gangliaReporter.setPort(gangliaPort);
        gangliaReporter.setGroup(gangliaGroup);
        gangliaReporter.setSystemMonitor(true);
        gangliaReporter.setJvmMonitor(false);
        gangliaReporter.setResinMonitor(false);
        gangliaReporter.setTomcatMonitor(false);
        return gangliaReporter;
    }
}
