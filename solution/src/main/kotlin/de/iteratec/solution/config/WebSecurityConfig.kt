package de.iteratec.solution.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain


@Configuration
class WebSecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeRequests {
                authorize("/pizza/image", authenticated)
                authorize("/user/**", authenticated)
                authorize(anyRequest, permitAll)
            }
            exceptionHandling {
                //authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.PAYMENT_REQUIRED)
            }
            httpBasic {  }
            csrf { disable() }
            headers {
                frameOptions { sameOrigin = true }
            }
        }

        return http.build()
    }

    @Bean
    fun userDetailsService() = InMemoryUserDetailsManager().apply {
        val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
        createUser(User.withUsername("user").passwordEncoder(passwordEncoder::encode).password("password").roles("user").build())
    }
}
