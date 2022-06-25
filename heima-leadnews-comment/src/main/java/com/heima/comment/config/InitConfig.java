package com.heima.comment.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.heima.common.jackson","com.heima.common.aliyun","com.heima.common.exception"})
public class InitConfig {
}
