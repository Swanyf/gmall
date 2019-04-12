package com.swan.gmall.config;

import com.swan.gmall.interceptor.LoginIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    LoginIntercepter loginIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注入拦截器，拦截所有请求
        registry.addInterceptor(loginIntercepter).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
