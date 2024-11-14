package com.z.dbes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by qinkuan on 2022/1/8.
 */
@Configuration
@ConfigurationProperties(prefix = "elasticsearch.rest")
public class ElasticsearchConfig extends BaseElasticsearchConfig {

}
