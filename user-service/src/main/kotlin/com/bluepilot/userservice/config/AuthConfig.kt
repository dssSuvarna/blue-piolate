package com.bluepilot.userservice.config

import com.bluepilot.configs.ApiConfig
import com.bluepilot.configs.CustomPermissionEvaluator
import com.bluepilot.filters.JwtTokenFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
class AuthConfig {

    @Autowired
    lateinit var apiConfig: ApiConfig

    @Autowired
    lateinit var permissionEvaluator: CustomPermissionEvaluator

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.addFilterBefore(JwtTokenFilter(apiConfig), UsernamePasswordAuthenticationFilter::class.java)
        return http.csrf ().disable()
            .authorizeHttpRequests().requestMatchers("**").permitAll()
            .and()
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun methodSecurityExpressionHandler(): MethodSecurityExpressionHandler? {
        val handler = DefaultMethodSecurityExpressionHandler()
        handler.setPermissionEvaluator(permissionEvaluator)
        return handler
    }
}