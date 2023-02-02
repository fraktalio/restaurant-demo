package com.fraktalio.restaurant.command.api

import com.fraktalio.restaurant.command.RestaurantMenuCuisine
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import java.math.BigDecimal
import java.util.*

data class MenuItemVO(val id: String, val name: String, val price: Money)

data class RestaurantMenuVO(
    @field:NotEmpty @field:Valid val menuItems: List<MenuItemVO>,
    val menuId: UUID = UUID.randomUUID(),
    val cuisine: RestaurantMenuCuisine = RestaurantMenuCuisine.GENERAL
)

data class RestaurantOrderDetails(val lineItems: List<RestaurantOrderLineItem>)

data class RestaurantOrderLineItem(val quantity: Int, val menuItemId: String, val name: String)

data class RestaurantId(val identifier: UUID = UUID.randomUUID()) {
    override fun toString(): String = identifier.toString()
}

data class RestaurantOrderId(val identifier: UUID = UUID.randomUUID()) {
    override fun toString(): String = identifier.toString()
}

const val ANONYMOUS = "anonymous"

data class Money(val amount: BigDecimal) {

    fun add(delta: Money): Money {
        return Money(amount.add(delta.amount))
    }

    fun isGreaterThanOrEqual(other: Money): Boolean {
        return amount >= other.amount
    }

    fun multiply(x: Int): Money {
        return Money(amount.multiply(BigDecimal(x)))
    }
}


