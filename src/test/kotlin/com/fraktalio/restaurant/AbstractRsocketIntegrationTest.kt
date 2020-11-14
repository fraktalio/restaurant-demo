package com.fraktalio.restaurant

import io.rsocket.core.RSocketConnector
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.rsocket.context.LocalRSocketServerPort
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder
import reactor.core.publisher.Flux
import java.net.URI
import java.time.Duration

abstract class AbstractRsocketIntegrationTest {

    companion object {

        lateinit var requester: RSocketRequester

        @JvmStatic
        @BeforeAll
        fun beforeAll(
            @Autowired builder: RSocketRequester.Builder,
            @LocalRSocketServerPort port: Int,
            @Autowired strategies: RSocketStrategies
        ) {
            val responder = RSocketMessageHandler.responder(
                strategies,
                ClientHandler()
            )


            requester = builder
                //.setupMetadata(credentials, mimeType)
                .rsocketStrategies { b: RSocketStrategies.Builder ->
                    b.encoder(
                        SimpleAuthenticationEncoder()
                    )
                }
                .rsocketConnector { connector: RSocketConnector -> connector.acceptor(responder) }
                .connectWebSocket(URI.create("ws://localhost:$port/rsocket"))
                .blockOptional().orElseThrow()
        }

        @JvmStatic
        @AfterAll
        fun tearDownOnce() {
            requester.rsocket()?.dispose()
        }

        internal class ClientHandler {

            @MessageMapping("client-status")
            fun statusUpdate(status: String): Flux<String> {
                return Flux.interval(Duration.ofSeconds(5))
                    .map { index ->
                        Runtime.getRuntime().freeMemory().toString()
                    }
            }
        }

    }

}
