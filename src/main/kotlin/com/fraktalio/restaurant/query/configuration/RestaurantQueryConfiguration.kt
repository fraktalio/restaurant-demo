package com.fraktalio.restaurant.query.configuration

import com.fraktalio.restaurant.web.configuration.Logging
import org.axonframework.config.EventProcessingConfigurer
import org.axonframework.eventhandling.PropagatingErrorHandler
import org.axonframework.messaging.interceptors.LoggingInterceptor
import org.axonframework.queryhandling.QueryBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
internal class RestaurantQueryConfiguration :
    Logging {

    /**************************************************/
    /* Register handler interceptors on the query bus */
    /**************************************************/

    @Autowired
    fun registerQueryInterceptorsOnBus(queryBus: QueryBus) {
        queryBus.registerHandlerInterceptor(LoggingInterceptor())
    }

    /*******************************************************************************************************************************/
    /* Register default listener invocation handler on the event processors */
    /* By default these exceptions are logged and processing continues with the next handler or message */
    /* We change this behaviour by configuring custom listener PropagatingErrorHandler to re-throw the exception to the processor. */

    /* https://docs.axoniq.io/reference-guide/axon-framework/events/event-processors#exceptions-raised-by-event-handler-methods */
    /*******************************************************************************************************************************/

    @Autowired
    fun configureError(config: EventProcessingConfigurer) {
        config.registerDefaultListenerInvocationErrorHandler { PropagatingErrorHandler.instance() }
    }
}
