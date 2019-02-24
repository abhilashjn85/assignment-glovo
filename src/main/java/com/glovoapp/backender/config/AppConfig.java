package com.glovoapp.backender.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * AppConfig for any app. The following configurations must be used:
 *    @Configuration
 *    @ComponentScan(basePackages = {Constants.BEANS_BASE_PACKAGE})
 *    @PropertySource(value = { "classpath:application.properties" })
 *    @EnableAspectJAutoProxy
 *    @EnableWebMvc
 *
 * This class should be extended by any module that wants to be deployment unit
 */
public abstract class AppConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private Environment environment;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
