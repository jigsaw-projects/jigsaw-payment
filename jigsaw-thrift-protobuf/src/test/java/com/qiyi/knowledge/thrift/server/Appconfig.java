/**
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi qiyipay Tool project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 ***/
package com.qiyi.knowledge.thrift.server;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

/**
 * @author lihengjun@qiyi.com
 * @time 5/15/17
 */
@Configuration
@Import(DataSourceAutoConfiguration.class)
@PropertySource("application.properties")
public class Appconfig {

    @Bean
    public TradeRepository tradeRepository(DataSource dataSource) {
        return new TradeRepository();
    }
}
