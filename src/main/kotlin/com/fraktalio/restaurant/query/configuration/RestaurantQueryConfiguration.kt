package com.fraktalio.restaurant.query.configuration

import com.fraktalio.restaurant.web.configuration.Logging
import org.axonframework.messaging.interceptors.LoggingInterceptor
import org.axonframework.queryhandling.QueryBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
internal class RestaurantQueryConfiguration :
    Logging {

    /************************************************/
    /* Register interceptors on the bus */
    /************************************************/

    @Autowired
    fun registerQueryInterceptorsOnBus(queryBus: QueryBus) {
        queryBus.registerHandlerInterceptor(LoggingInterceptor())
    }
}
