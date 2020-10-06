package com.fraktalio.restaurant.web.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver


@Configuration
@EnableReactiveMethodSecurity
internal class RestaurantSecurityConfiguration {

    @Bean
    fun authentication(): MapReactiveUserDetailsService {
        //This is NOT intended for production use (it is intended for getting started experience only)
        val user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("pass")
            .roles("USER")
            .build()
        val manager = User.withDefaultPasswordEncoder()
            .username("manager")
            .password("pass")
            .roles("MANAGER")
            .build()
        return MapReactiveUserDetailsService(user, manager)
    }
}
