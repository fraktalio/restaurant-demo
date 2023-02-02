package com.fraktalio.restaurant.query.repository.model

import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
internal data class MenuEntity(
    @Id var id: String,
    var menuVersion: Int,
    var restaurantId: String,
    var active: Boolean,
    @ElementCollection var menuItems: List<MenuItemEmbedable>
)

