package com.fraktalio.restaurant

import com.fraktalio.restaurant.command.api.RestaurantId
import com.fraktalio.restaurant.query.api.RestaurantModel
import com.fraktalio.restaurant.util.AxonSpringContextInitializer
import com.fraktalio.restaurant.web.api.CreateRestaurantRequest
import com.fraktalio.restaurant.web.api.MenuItemRequest
import io.rsocket.metadata.WellKnownMimeType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.retrieveFlux
import org.springframework.messaging.rsocket.retrieveMono
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.MimeTypeUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.math.BigDecimal

@SpringBootTest
@ContextConfiguration(initializers = [AxonSpringContextInitializer::class])
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
internal class RestaurantApplicationIT : AbstractRsocketIntegrationTest() {

    val mimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)
    val manager = UsernamePasswordMetadata("manager", "pass")
    val user = UsernamePasswordMetadata("user", "pass")

    @Test
    fun `create and browse the restaurant - access denied for the regular user`() {

        val menuItem = MenuItemRequest("id", "name", BigDecimal.TEN)
        val result: Mono<RestaurantId> = requester
            .route("restaurants.create")
            .metadata(user, mimeType)
            .data(CreateRestaurantRequest("restaurantName", arrayListOf(menuItem)))
            .retrieveMono()
        // Assert that the user 'user' is DENIED access to the method.
        StepVerifier
            .create(result)
            .verifyErrorMessage("Denied")

        val resultList1: Mono<List<RestaurantModel>> = requester
            .route("restaurants.get")
            .metadata(user, mimeType)
            .retrieveMono()
        // Assert that the user 'user' is DENIED access to the method "restaurants.get".
        StepVerifier
            .create(resultList1)
            .verifyErrorMessage("Denied")

        val resultList2: Flux<RestaurantModel> = requester
            .route("restaurants.get")
            .metadata(user, mimeType)
            .retrieveFlux()
        // Assert that the user 'user' is DENIED access to the method "restaurants.get (STREAM)".
        StepVerifier
            .create(resultList2)
            .verifyErrorMessage("Denied")

        val result2: Flux<RestaurantModel> = requester
            .route("restaurants.65e59005-f867-4f73-93f5-15b473c42817.get")
            .metadata(user, mimeType)
            .retrieveFlux(RestaurantModel::class.java)
        // Assert that the user 'user' is DENIED access to the method "restaurants.${restaurantId}.get (STREAM)".
        StepVerifier
            .create(result2)
            .verifyErrorMessage("Denied")
    }

    @Test
    fun `create and browse the restaurant - access granted for the manager`() {

        val menuItem = MenuItemRequest("id", "name", BigDecimal.TEN)
        val result: Mono<RestaurantId> = requester
            .route("restaurants.create")
            .metadata(manager, mimeType)
            .data(CreateRestaurantRequest("restaurantName", arrayListOf(menuItem)))
            .retrieveMono()
        // Assert that the user 'manager' is granted access to the method.
        StepVerifier
            .create(result)
            .expectNextCount(1)
            .verifyComplete()


        val resultList2: Mono<List<RestaurantModel>> = requester
            .route("restaurants.get")
            .metadata(manager, mimeType)
            .retrieveMono()
        // Assert that the user 'manager' is granted access to the route "restaurants.get".
        StepVerifier
            .create(resultList2)
            .assertNext { Assertions.assertTrue(it.stream().anyMatch { i -> i.name == "restaurantName" }) }
            .verifyComplete()

        val resultList4: Flux<RestaurantModel> = requester
            .route("restaurants.get")
            .metadata(manager, mimeType)
            .retrieveFlux()
        // Assert that the user 'manager' is granted access to the route "restaurants.get (STREAM)".
        StepVerifier
            .create(resultList4)
            .assertNext { Assertions.assertTrue(it.name == "restaurantName") }
            .thenCancel()
            .verify()

        fun result2(restaurantId: String): Flux<RestaurantModel> = requester
            .route("restaurants.${restaurantId}.get")
            .metadata(manager, mimeType)
            .retrieveFlux(RestaurantModel::class.java)
        // Assert that the user 'manager' is granted access to the route "restaurants.${restaurantId}.get (STREAM)".
        StepVerifier
            .create(resultList4.map { it.id }.flatMap { i -> result2(i) })
            .expectNextCount(1)
            .thenCancel()
            .verify()

    }
}

