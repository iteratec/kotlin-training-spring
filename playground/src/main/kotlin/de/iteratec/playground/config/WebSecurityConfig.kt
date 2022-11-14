package de.iteratec.playground.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain


@Configuration
class WebSecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeRequests {
                authorize(anyRequest, permitAll)
            }
            csrf { disable() }
            headers {
                frameOptions { sameOrigin = true }
            }
        }

        return http.build()
    }
}
