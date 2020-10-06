package com.fraktalio.restaurant.query.repository

import com.fraktalio.restaurant.query.repository.model.MenuEntity
import org.springframework.data.repository.PagingAndSortingRepository

internal interface MenuRepository : PagingAndSortingRepository<MenuEntity, String>
