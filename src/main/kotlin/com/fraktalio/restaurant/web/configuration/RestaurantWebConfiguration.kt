package com.fraktalio.restaurant.web.configuration

import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
internal class RestaurantWebConfiguration :
    Logging {

    /***************************************************/
    /* Register a dispatch interceptors on the gateway */
    /***************************************************/

    @Autowired
    fun registerCommandInterceptorsOnReactiveGateway(reactorCommandGateway: ReactorCommandGateway) {
        reactorCommandGateway.registerDispatchInterceptor(SpringSecurityReactorMessageDispatchInterceptor())
        reactorCommandGateway.registerDispatchInterceptor(LoggingReactorMessageDispatchInterceptor())

    }

    @Autowired
    fun registerQueryInterceptorsOnReactiveGateway(reactorQueryGateway: ReactorQueryGateway) {
        reactorQueryGateway.registerDispatchInterceptor(SpringSecurityReactorMessageDispatchInterceptor())
        reactorQueryGateway.registerDispatchInterceptor(LoggingReactorMessageDispatchInterceptor())
    }

}
