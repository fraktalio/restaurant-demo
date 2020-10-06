package com.fraktalio.restaurant.query.repository.model

import java.math.BigDecimal
import javax.persistence.ElementCollection
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.Id

@Entity
internal data class RestaurantEntity(
    @Id var id: String, var aggregateVersion: Long,
    var name: String,
    @ElementCollection var menuItems: List<MenuItemEmbedable>?
)

@Embeddable
internal data class MenuItemEmbedable(var menuId: String, var name: String, var price: BigDecimal)

