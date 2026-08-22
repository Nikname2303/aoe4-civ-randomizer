package com.aoe4.randomizer.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NoKeepAliveStaticAssetFilterConfiguration {

    @Bean
    public FilterRegistrationBean<NoKeepAliveStaticAssetFilter> noKeepAliveForIconsFilter() {
        FilterRegistrationBean<NoKeepAliveStaticAssetFilter> registration =
                new FilterRegistrationBean<>(new NoKeepAliveStaticAssetFilter());
        registration.addUrlPatterns("/images/*");
        registration.setName("noKeepAliveForIconsFilter");
        return registration;
    }
}
