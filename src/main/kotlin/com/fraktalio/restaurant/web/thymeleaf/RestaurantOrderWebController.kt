package com.fraktalio.restaurant.web.thymeleaf

import com.fraktalio.restaurant.command.api.*
import com.fraktalio.restaurant.query.api.FindAllRestaurantOrdersByUserQuery
import com.fraktalio.restaurant.query.api.FindAllRestaurantOrdersQuery
import com.fraktalio.restaurant.query.api.RestaurantOrderModel
import com.fraktalio.restaurant.web.api.CreateRestaurantOrderRequest
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid


@Controller
internal class RestaurantOrderWebController(
    private val reactorCommandGateway: ReactorCommandGateway,
    private val reactorQueryGateway: ReactorQueryGateway
) {

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/restaurant-user-orders")
    fun placeRestaurantOrder(
        @Valid @ModelAttribute request: CreateRestaurantOrderRequest,
        bindingResult: BindingResult,
        model: Model
    ): Mono<String> {
        val result: Mono<RestaurantOrderId> = reactorCommandGateway.send(
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
        return Mono
            .just(bindingResult)
            .map { it.hasErrors() }
            .filter { it }
            .flatMap { Mono.just("restaurant-user-orders") }
            .switchIfEmpty(result.thenReturn("redirect:/restaurant-user-orders"))
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/restaurant-orders/{restaurantOrderId}/mark-prepared")
    fun markRestaurantOrderAsPrepared(@PathVariable restaurantOrderId: UUID): Mono<String> =
        reactorCommandGateway.send<Void>(MarkRestaurantOrderAsPreparedCommand(RestaurantOrderId(restaurantOrderId)))
            .thenReturn("redirect:/restaurant-orders")

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/restaurant-orders")
    fun restaurantOrders(model: Model): Mono<String> {
        model.addAttribute("createRestaurantOrderRequest", CreateRestaurantOrderRequest(UUID.randomUUID()))
        return Mono.just("restaurant-orders")
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/restaurant-user-orders")
    fun restaurantUserOrders(model: Model): Mono<String> {
        model.addAttribute("createRestaurantOrderRequest", CreateRestaurantOrderRequest(UUID.randomUUID()))
        return Mono.just("restaurant-user-orders")
    }


    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/restaurant-orders-sse", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getRestaurantOrdersSubscribe(model: Model, @AuthenticationPrincipal user: UserDetails): Mono<String> {
        val result: Flux<RestaurantOrderModel> =
            reactorQueryGateway.subscriptionQueryMany(
                FindAllRestaurantOrdersQuery(),
                RestaurantOrderModel::class.java
            )
        model.addAttribute("restaurantOrders", ReactiveDataDriverContextVariable(result, 1))
        return Mono.just("sse/restaurant-orders-sse")
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/restaurant-user-orders-sse", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getRestaurantUserOrdersSubscribe(model: Model, @AuthenticationPrincipal user: UserDetails): Mono<String> {
        val result: Flux<RestaurantOrderModel> =
            reactorQueryGateway.subscriptionQueryMany(
                FindAllRestaurantOrdersByUserQuery(user.username),
                RestaurantOrderModel::class.java
            )
        model.addAttribute("restaurantOrders", ReactiveDataDriverContextVariable(result, 1))
        return Mono.just("sse/restaurant-user-orders-sse")
    }

}
