package com.fraktalio.restaurant.web.rsocket

import com.fraktalio.restaurant.command.api.ActivateRestaurantMenuCommand
import com.fraktalio.restaurant.command.api.PassivateRestaurantMenuCommand
import com.fraktalio.restaurant.command.api.RestaurantId
import com.fraktalio.restaurant.query.api.FindAllMenusQuery
import com.fraktalio.restaurant.query.api.FindMenuQuery
import com.fraktalio.restaurant.query.api.MenuModel
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
internal class MenuRSocketController(
    private val reactorQueryGateway: ReactorQueryGateway,
    private val reactorCommandGateway: ReactorCommandGateway
) {

    // REQUEST - RESPONSE (Fire and Forget)
    @MessageMapping("restaurants.{restaurantId}.menus.{menuId}.activate")
    @PreAuthorize("hasRole('MANAGER')")
    fun activateMenu(@DestinationVariable restaurantId: UUID, @DestinationVariable menuId: UUID): Mono<Unit> =
        reactorCommandGateway.send(
            ActivateRestaurantMenuCommand(
                menuId,
                RestaurantId(restaurantId)
            )
        )

    // REQUEST - RESPONSE (Fire and Forget)
    @MessageMapping("restaurants.{restaurantId}.menus.{menuId}.passivate")
    @PreAuthorize("hasRole('MANAGER')")
    fun passivateMenu(@DestinationVariable restaurantId: UUID, @DestinationVariable menuId: UUID): Mono<Unit> =
        reactorCommandGateway.send(
            PassivateRestaurantMenuCommand(
                menuId,
                RestaurantId(restaurantId)
            )
        )

    // REQUEST - RESPONSE
    @MessageMapping("restaurants.menus.{menuId}.get")
    @PreAuthorize("hasRole('MANAGER') or hasRole('USER')")
    fun getMenu(@DestinationVariable menuId: UUID): Mono<MenuModel> =
        reactorQueryGateway.query(
            FindMenuQuery(menuId),
            ResponseTypes.instanceOf(MenuModel::class.java)
        )

    // REQUEST - STREAM
    @MessageMapping("restaurants.menus.{menuId}.get")
    @PreAuthorize("hasRole('MANAGER') or hasRole('USER')")
    fun getMenuSubscribe(@DestinationVariable menuId: UUID): Flux<MenuModel> =
        reactorQueryGateway.subscriptionQuery(
            FindMenuQuery(menuId),
            MenuModel::class.java
        )

    // REQUEST - RESPONSE
    @MessageMapping("restaurants.menus.get")
    @PreAuthorize("hasRole('MANAGER') or hasRole('USER')")
    fun getMenus(): Mono<List<MenuModel>> =
        reactorQueryGateway.query(
            FindAllMenusQuery(),
            ResponseTypes.multipleInstancesOf(MenuModel::class.java)
        )

    // REQUEST - STREAM
    @MessageMapping("restaurants.menus.get")
    @PreAuthorize("hasRole('MANAGER') or hasRole('USER')")
    fun getMenusSubscribe(): Flux<MenuModel> =
        reactorQueryGateway.subscriptionQueryMany(FindAllMenusQuery(), MenuModel::class.java)

}
