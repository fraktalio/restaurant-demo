package com.fraktalio.restaurant.command.api

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*
import javax.validation.Valid


abstract class RestaurantCommand(
    open val targetAggregateIdentifier: RestaurantId
)

abstract class RestaurantOrderCommand(
    open val targetAggregateIdentifier: RestaurantOrderId
)

data class CreateRestaurantCommand(
    val name: String,
    @field:Valid val menu: RestaurantMenuVO,
    @TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantId = RestaurantId()
) : RestaurantCommand(targetAggregateIdentifier)

data class ChangeRestaurantMenuCommand(
    @field:Valid val menu: RestaurantMenuVO,
    @TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantId = RestaurantId()
) : RestaurantCommand(targetAggregateIdentifier)

data class ActivateRestaurantMenuCommand(
    val menuId: UUID,
    @TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantId
) : RestaurantCommand(targetAggregateIdentifier)

data class PassivateRestaurantMenuCommand(
    val menuId: UUID,
    @TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantId
) : RestaurantCommand(targetAggregateIdentifier)

data class PlaceRestaurantOrderCommand(
    @TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantId,
    @field:Valid val orderDetails: RestaurantOrderDetails,
    val restaurantOrderId: RestaurantOrderId = RestaurantOrderId()
) : RestaurantCommand(targetAggregateIdentifier)

data class MarkRestaurantOrderAsPreparedCommand(
    @TargetAggregateIdentifier override val targetAggregateIdentifier: RestaurantOrderId
) : RestaurantOrderCommand(targetAggregateIdentifier)
