package com.fraktalio.restaurant.query.repository

import com.fraktalio.restaurant.query.repository.model.RestaurantEntity
import org.springframework.data.repository.PagingAndSortingRepository

internal interface RestaurantRepository : PagingAndSortingRepository<RestaurantEntity, String>
