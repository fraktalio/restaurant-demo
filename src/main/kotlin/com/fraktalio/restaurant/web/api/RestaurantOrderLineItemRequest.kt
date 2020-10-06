package com.fraktalio.restaurant.web.api

data class RestaurantOrderLineItemRequest(val quantity: Int, val menuItemId: String, val name: String)
