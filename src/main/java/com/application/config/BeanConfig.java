package com.application.config;

import com.application.esdao.EsDao;
import com.application.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Autowired
    private EsDao esDao;

    @Autowired
    private QueryService queryService;

    @Bean
    public EsDao esDao() {
        return new EsDao();
    }

    @Bean
    public QueryService queryService() {
        return new QueryService();
    }
}
