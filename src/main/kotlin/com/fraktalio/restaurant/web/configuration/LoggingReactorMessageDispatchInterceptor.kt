package com.fraktalio.restaurant.web.configuration

import org.axonframework.extensions.reactor.messaging.ReactorMessageDispatchInterceptor
import org.axonframework.messaging.Message
import reactor.core.publisher.Mono

class LoggingReactorMessageDispatchInterceptor<M : Message<*>> : Logging,
    ReactorMessageDispatchInterceptor<M> {
    override fun intercept(message: Mono<M>): Mono<M> {
        return message.doOnNext {
            logger().info("Dispatched message ${it.payloadType.simpleName} with metadata ${it.metaData}")
        }
    }
}
