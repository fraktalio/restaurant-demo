package com.fraktalio.restaurant.query.api

import java.util.*

data class FindRestaurantQuery(val restaurantId: RestaurantId)

data class FindMenuQuery(val menuId: UUID)

class FindAllMenusQuery

data class FindRestaurantOrderQuery(val restaurantOrderId: RestaurantOrderId)

class FindAllRestaurantsQuery

class FindAllRestaurantOrdersQuery

data class FindAllRestaurantOrdersByUserQuery(val user: String)
