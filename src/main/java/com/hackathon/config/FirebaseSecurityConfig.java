package com.hackathon.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseSecurityConfig {

    @Bean
    public FilterRegistrationBean<FirebaseAuthenticationFilter> firebaseAuthenticationFilter() {
        FilterRegistrationBean<FirebaseAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FirebaseAuthenticationFilter());
        registrationBean.addUrlPatterns("/recommendations", "/portfolio", "/data");  // Protect specific endpoints
        return registrationBean;
    }
}
