package com.fraktalio.restaurant.web.api

import java.math.BigDecimal

/**
 * A Menu item request
 */
data class MenuItemRequest(var id: String, var name: String, var price: BigDecimal)
