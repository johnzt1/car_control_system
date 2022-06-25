package com.heima.search.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.heima.common.jackson","com.heima.common.aliyun",
        "com.heima.common.exception","com.heima.common.threadpool","com.heima.common.redis"})
public class InitConfig {
}
