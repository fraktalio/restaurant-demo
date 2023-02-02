package com.fraktalio.restaurant.web.thymeleaf

import com.fraktalio.restaurant.command.api.ActivateRestaurantMenuCommand
import com.fraktalio.restaurant.command.api.PassivateRestaurantMenuCommand
import com.fraktalio.restaurant.command.api.RestaurantId
import com.fraktalio.restaurant.query.api.FindAllMenusQuery
import com.fraktalio.restaurant.query.api.MenuModel
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Controller
internal class MenuWebController(

    private val reactorCommandGateway: ReactorCommandGateway,
    private val reactorQueryGateway: ReactorQueryGateway
) {

    @PreAuthorize("hasRole('MANAGER') or hasRole('USER')")
    @GetMapping("/menus")
    fun restaurants(model: Model): Mono<String> {
        return Mono.just("menus")
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/restaurants/{restaurantId}/menus/{menuId}/activate")
    fun activateMenu(
        @PathVariable restaurantId: UUID,
        @PathVariable menuId: UUID
    ): Mono<String> =
        reactorCommandGateway.send<Void>(
            ActivateRestaurantMenuCommand(
                menuId,
                RestaurantId(restaurantId)
            )
        ).thenReturn("redirect:/menus")


    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/restaurants/{restaurantId}/menus/{menuId}/passivate")
    fun passivateMenu(
        @PathVariable restaurantId: UUID,
        @PathVariable menuId: UUID
    ): Mono<String> =
        reactorCommandGateway.send<Void>(
            PassivateRestaurantMenuCommand(
                menuId,
                RestaurantId(restaurantId)
            )
        ).thenReturn("redirect:/menus")


    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/menus-sse", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun restaurantsSSE(model: Model): Mono<String> {
        val result: Flux<MenuModel> =
            reactorQueryGateway.subscriptionQueryMany(FindAllMenusQuery(), MenuModel::class.java)
        model.addAttribute("menus", ReactiveDataDriverContextVariable(result, 1))
        return Mono.just("sse/menus-sse")
    }

}


