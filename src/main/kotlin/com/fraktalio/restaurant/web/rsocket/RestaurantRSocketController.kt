package com.fraktalio.restaurant.web.rsocket

import com.fraktalio.restaurant.command.api.*
import com.fraktalio.restaurant.query.api.FindAllRestaurantsQuery
import com.fraktalio.restaurant.query.api.FindRestaurantQuery
import com.fraktalio.restaurant.query.api.RestaurantModel
import com.fraktalio.restaurant.web.api.CreateRestaurantRequest
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import kotlin.collections.ArrayList


@Controller
internal class RestaurantRSocketController(

    private val reactorCommandGateway: ReactorCommandGateway,
    private val reactorQueryGateway: ReactorQueryGateway
) {
    // REQUEST - RESPONSE
    @MessageMapping("restaurants.create")
    @PreAuthorize("hasRole('MANAGER')")
    fun createRestaurant(request: CreateRestaurantRequest): Mono<RestaurantId> {

        val menuItems = ArrayList<MenuItemVO>()
        for ((id, name, price) in request.menuItems) {
            val item = MenuItemVO(id, name, Money(price))
            menuItems.add(item)
        }
        val menu = RestaurantMenuVO(menuItems)
        val command = CreateRestaurantCommand(request.name, menu)

        return reactorCommandGateway.send(command)
    }

    // REQUEST - RESPONSE
    @MessageMapping("restaurants.{restaurantId}.get")
    @PreAuthorize("hasRole('MANAGER')")
    fun getRestaurant(@DestinationVariable restaurantId: UUID): Mono<RestaurantModel> =
        reactorQueryGateway.query(
            FindRestaurantQuery(com.fraktalio.restaurant.query.api.RestaurantId(restaurantId)),
            ResponseTypes.instanceOf(RestaurantModel::class.java)
        )

    // REQUEST - STREAM
    @MessageMapping("restaurants.{restaurantId}.get")
    @PreAuthorize("hasRole('MANAGER')")
    fun getRestaurantSubscribe(@DestinationVariable restaurantId: UUID): Flux<RestaurantModel> =
        reactorQueryGateway.subscriptionQuery(
            FindRestaurantQuery(com.fraktalio.restaurant.query.api.RestaurantId(restaurantId)),
            RestaurantModel::class.java
        )


    // REQUEST - RESPONSE
    @MessageMapping("restaurants.get")
    @PreAuthorize("hasRole('MANAGER')")
    fun getRestaurants(): Mono<List<RestaurantModel>> =
        reactorQueryGateway.query(
            FindAllRestaurantsQuery(),
            ResponseTypes.multipleInstancesOf(RestaurantModel::class.java)
        )


    // REQUEST - STREAM
    @MessageMapping("restaurants.get")
    @PreAuthorize("hasRole('MANAGER')")
    fun getRestaurantsSubscribe(): Flux<RestaurantModel> =
        reactorQueryGateway.subscriptionQueryMany(FindAllRestaurantsQuery(), RestaurantModel::class.java)

}


