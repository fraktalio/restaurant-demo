package com.fraktalio.restaurant.web.rsocket

import com.fraktalio.restaurant.command.api.*
import com.fraktalio.restaurant.query.api.FindAllRestaurantOrdersQuery
import com.fraktalio.restaurant.query.api.FindRestaurantOrderQuery
import com.fraktalio.restaurant.query.api.RestaurantOrderModel
import com.fraktalio.restaurant.web.api.CreateRestaurantOrderRequest
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


@Controller
internal class RestaurantOrderRSocketController(
    private val reactorCommandGateway: ReactorCommandGateway,
    private val reactorQueryGateway: ReactorQueryGateway
) {
    // REQUEST-RESPONSE
    @PreAuthorize("hasRole('MANAGER') or hasRole('USER')")
    @MessageMapping("restaurant-orders.create")
    fun placeRestaurantOrder(request: CreateRestaurantOrderRequest): Mono<RestaurantOrderId> =
        reactorCommandGateway.send(
            PlaceRestaurantOrderCommand(
                RestaurantId(request.restaurantId),
                RestaurantOrderDetails(request.lineItems.map {
                    RestaurantOrderLineItem(
                        it.quantity,
                        it.menuItemId,
                        it.name
                    )
                })
            )
        )

    // REQUEST-RESPONSE (Fire and Forget)
    @MessageMapping("restaurant-orders.{restaurantOrderId}.mark-prepared")
    @PreAuthorize("hasRole('MANAGER')")
    fun markRestaurantOrderAsPrepared(@DestinationVariable restaurantOrderId: UUID): Mono<Unit> =
        reactorCommandGateway.send(MarkRestaurantOrderAsPreparedCommand(RestaurantOrderId(restaurantOrderId)))

    // REQUEST - RESPONSE
    @MessageMapping("restaurant-orders.{restaurantOrderId}.get")
    @PreAuthorize("hasRole('MANAGER')")
    fun getRestaurantOrder(@DestinationVariable restaurantOrderId: UUID): Mono<RestaurantOrderModel> =
        reactorQueryGateway.query(
            FindRestaurantOrderQuery(com.fraktalio.restaurant.query.api.RestaurantOrderId(restaurantOrderId)),
            ResponseTypes.instanceOf(RestaurantOrderModel::class.java)
        )

    // REQUEST - STREAM
    @MessageMapping("restaurant-orders.{restaurantOrderId}.get")
    @PreAuthorize("hasRole('MANAGER')")
    fun getRestaurantOrderSubscribe(@DestinationVariable restaurantOrderId: UUID): Flux<RestaurantOrderModel> =
        reactorQueryGateway.subscriptionQuery(
            FindRestaurantOrderQuery(com.fraktalio.restaurant.query.api.RestaurantOrderId(restaurantOrderId)),
            RestaurantOrderModel::class.java
        )

    // REQUEST - RESPONSE
    @MessageMapping("restaurant-orders.get")
    @PreAuthorize("hasRole('MANAGER')")
    fun getRestaurantOrders(): Mono<List<RestaurantOrderModel>> =
        reactorQueryGateway.query(
            FindAllRestaurantOrdersQuery(),
            ResponseTypes.multipleInstancesOf(RestaurantOrderModel::class.java)
        )

    // REQUEST - STREAM
    @MessageMapping("restaurant-orders.get")
    @PreAuthorize("hasRole('MANAGER')")
    fun getRestaurantOrdersSubscribe(): Flux<RestaurantOrderModel> =
        reactorQueryGateway.subscriptionQueryMany(FindAllRestaurantOrdersQuery(), RestaurantOrderModel::class.java)

}

