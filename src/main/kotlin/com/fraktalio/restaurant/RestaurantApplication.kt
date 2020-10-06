package com.fraktalio.restaurant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication


@SpringBootApplication
@ConfigurationPropertiesScan
class RestaurantApplication

fun main(args: Array<String>) {
    runApplication<RestaurantApplication>(*args)
}

