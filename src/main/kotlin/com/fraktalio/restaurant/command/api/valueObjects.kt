package com.fraktalio.restaurant.command.api

import com.fraktalio.restaurant.command.RestaurantMenuCuisine
import java.math.BigDecimal
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

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

data class AuditEntry(
    val who: String = ANONYMOUS,
    val `when`: Date = Calendar.getInstance().time,
    val authorities: Collection<String> = listOf(ANONYMOUS)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuditEntry

        if (who != other.who) return false
        if (authorities != other.authorities) return false

        return true
    }

    override fun hashCode(): Int {
        var result = who.hashCode()
        result = 31 * result + authorities.hashCode()
        return result
    }
}

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


