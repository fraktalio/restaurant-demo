package com.fraktalio.restaurant.util

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer

class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName) {

    fun getURL(): String = this.containerIpAddress + ":" + this.getMappedPort(8124)
}

class KPostgreSQLContainer(imageName: String) : PostgreSQLContainer<KPostgreSQLContainer>(imageName)
