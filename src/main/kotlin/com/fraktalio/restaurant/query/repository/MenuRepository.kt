package com.fraktalio.restaurant.query.repository

import com.fraktalio.restaurant.query.repository.model.MenuEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface MenuRepository : JpaRepository<MenuEntity, String>
