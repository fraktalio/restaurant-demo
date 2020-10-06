package com.fraktalio.restaurant.web.api

import java.util.*

/**
 * A request for creating a Restaurant(Kitchen) order
 */
data class CreateRestaurantOrderRequest(
    var restaurantId: UUID,
    val lineItems: List<RestaurantOrderLineItemRequest> = arrayListOf(
        RestaurantOrderLineItemRequest(1, "1", "Sarma"),
        RestaurantOrderLineItemRequest(2, "2", "Kiselo mleko")
    )

)
