package com.fraktalio.restaurant.web.api

import java.math.BigDecimal
import javax.validation.constraints.NotBlank

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
