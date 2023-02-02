package com.fraktalio.restaurant.query.repository

import com.fraktalio.restaurant.query.repository.model.RestaurantorderEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface RestaurantOrderRepository : JpaRepository<RestaurantorderEntity, String> {
    fun findByCustomer(customer: String): List<RestaurantorderEntity>
}
