package com.fraktalio.restaurant.query.repository.model


import com.fraktalio.restaurant.query.api.RestaurantOrderState
import javax.persistence.*

@Entity
internal data class RestaurantorderEntity(
    @Id var id: String,
    var aggregateVersion: Long,
    @ElementCollection var lineItems: List<RestaurantOrderItemEmbedable>?,
    var restaurantId: String,
    @Enumerated(EnumType.STRING) var state: RestaurantOrderState,
    var customer: String
)

@Embeddable
internal data class RestaurantOrderItemEmbedable(var menuId: String, var name: String, var quantity: Int)

