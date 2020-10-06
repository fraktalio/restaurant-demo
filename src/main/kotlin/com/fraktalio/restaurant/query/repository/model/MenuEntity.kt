package com.fraktalio.restaurant.query.repository.model

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id

@Entity
internal data class MenuEntity(
    @Id var id: String,
    var menuVersion: Int,
    var restaurantId: String,
    var active: Boolean,
    @ElementCollection var menuItems: List<MenuItemEmbedable>
)

