package com.fraktalio.restaurant.query.repository.model

import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.math.BigDecimal

@Entity
internal data class RestaurantEntity(
    @Id var id: String, var aggregateVersion: Long,
    var name: String,
    @ElementCollection var menuItems: List<MenuItemEmbedable>?
)

@Embeddable
internal data class MenuItemEmbedable(var menuId: String, var name: String, var price: BigDecimal)

