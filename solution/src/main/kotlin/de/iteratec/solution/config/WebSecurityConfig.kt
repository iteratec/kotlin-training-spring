package de.iteratec.solution.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain


@Configuration
class WebSecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .authorizeHttpRequests()
        .anyRequest()
        .permitAll()
        .and().csrf().disable()
        .build()
}