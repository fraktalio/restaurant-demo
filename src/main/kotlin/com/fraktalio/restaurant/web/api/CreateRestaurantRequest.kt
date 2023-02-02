package com.fraktalio.restaurant.web.api

import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

/**
 * A request for creating a Restaurant
 */
data class CreateRestaurantRequest(
    @field:NotBlank var name: String = "serbian restaurant",
    var menuItems: MutableList<MenuItemRequest> = arrayListOf(
        MenuItemRequest("1", "Sarma", BigDecimal.TEN),
        MenuItemRequest("2", "Kiselo mleko", BigDecimal.valueOf(2)),
        MenuItemRequest("3", "Srpska salata", BigDecimal.valueOf(3))
    )
)
