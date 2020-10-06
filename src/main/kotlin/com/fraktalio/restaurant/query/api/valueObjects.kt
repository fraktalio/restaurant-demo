package com.fraktalio.restaurant.query.api

import java.io.Serializable
import java.math.BigDecimal
import java.util.*

enum class RestaurantOrderState {
    CREATED, PREPARED, CANCELLED
}

enum class RestaurantState {
    OPEN, CLOSED
}

data class RestaurantId(val identifier: UUID) {

    override fun toString(): String = identifier.toString()
}

data class RestaurantOrderId(val identifier: UUID) {

    override fun toString(): String = identifier.toString()
}

open class RestaurantModel(
    val id: String,
    val aggregateVersion: Long,
    val name: String,
    val menuItems: List<MenuItemModel>?
) : Serializable


open class MenuItemModel(val menuId: String, val name: String, val price: BigDecimal) : Serializable
open class MenuModel(
    val id: String,
    val menuVersion: String,
    val restaurantId: String,
    val active: Boolean,
    val menuItems: List<MenuItemModel>
) : Serializable

open class RestaurantOrderModel(
    val id: String,
    val aggregateVersion: Long,
    val lineItems: List<RestaurantOrderItemModel>,
    val restaurantId: String,
    val state: RestaurantOrderState
)

open class RestaurantOrderItemModel(val menuId: String, val name: String, val quantity: Int)
