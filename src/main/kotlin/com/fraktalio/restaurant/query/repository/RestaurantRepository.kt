package com.fraktalio.restaurant.query.repository

import com.fraktalio.restaurant.query.repository.model.RestaurantEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface RestaurantRepository : JpaRepository<RestaurantEntity, String>
