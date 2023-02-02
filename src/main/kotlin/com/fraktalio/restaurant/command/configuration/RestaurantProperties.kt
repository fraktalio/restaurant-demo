package com.fraktalio.restaurant.command.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "restaurant")
internal data class RestaurantProperties(
    val snapshotTriggerTresholdRestaurant: Int,
    val snapshotTriggerTresholdRestaurantOrder: Int
)
