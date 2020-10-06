package com.fraktalio.restaurant.web.thymeleaf

import com.fraktalio.restaurant.command.api.*
import com.fraktalio.restaurant.query.api.FindAllRestaurantsQuery
import com.fraktalio.restaurant.query.api.RestaurantModel
import com.fraktalio.restaurant.web.api.CreateRestaurantRequest
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid


@Controller
internal class RestaurantWebController(

    private val reactorCommandGateway: ReactorCommandGateway,
    private val reactorQueryGateway: ReactorQueryGateway
) {

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/restaurants")
    fun restaurants(model: Model): Mono<String> {
        model.addAttribute("createRestaurantRequest", CreateRestaurantRequest())
        return Mono.just("restaurants")
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/restaurants")
    fun addRestaurant(
        @Valid @ModelAttribute createRestaurantRequest: CreateRestaurantRequest,
        bindingResult: BindingResult,
        model: Model
    ): Mono<String> {

        val menuItems = ArrayList<MenuItemVO>()
        for ((id, name, price) in createRestaurantRequest.menuItems) {
            val item = MenuItemVO(id, name, Money(price))
            menuItems.add(item)
        }
        val menu = RestaurantMenuVO(menuItems)
        val command = CreateRestaurantCommand(createRestaurantRequest.name, menu)
        val result: Mono<RestaurantId> = reactorCommandGateway.send(command)

        return Mono
            .just(bindingResult)
            .map { it.hasErrors() }
            .filter { it }
            .flatMap { Mono.just("restaurants") }
            .switchIfEmpty(result.thenReturn("redirect:/restaurants"))

    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/restaurants-sse", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun restaurantsSSE(model: Model): Mono<String> {
        val result: Flux<RestaurantModel> =
            reactorQueryGateway.subscriptionQueryMany(FindAllRestaurantsQuery(), RestaurantModel::class.java)
        model.addAttribute("restaurants", ReactiveDataDriverContextVariable(result, 1))
        return Mono.just("sse/restaurants-sse")
    }

        @PreAuthorize("hasRole('MANAGER') or hasRole('USER')")
    @GetMapping("/restaurant-options-sse", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun restaurantOptionsSSE(model: Model): Mono<String> {
        val result: Flux<RestaurantModel> =
            reactorQueryGateway.subscriptionQueryMany(FindAllRestaurantsQuery(), RestaurantModel::class.java)
        model.addAttribute("restaurants", ReactiveDataDriverContextVariable(result, 1))
        return Mono.just("sse/restaurant-options-sse")
    }


}


