package com.fraktalio.restaurant.query.repository

import com.fraktalio.restaurant.query.repository.model.RestaurantorderEntity
import org.springframework.data.repository.PagingAndSortingRepository

internal interface RestaurantOrderRepository : PagingAndSortingRepository<RestaurantorderEntity, String> {
    fun findByCustomer(customer: String): List<RestaurantorderEntity>
}
