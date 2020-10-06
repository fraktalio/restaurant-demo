package com.fraktalio.restaurant.util

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.wait.strategy.Wait
import java.time.Duration
import java.time.temporal.ChronoUnit

class AxonSpringContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    val axonServerTestContainer = KGenericContainer("axoniq/axonserver")
        .withExposedPorts(8024, 8124)
        .waitingFor(Wait.forHttp("/actuator/info").forPort(8024))
        .withStartupTimeout(Duration.of(60L, ChronoUnit.SECONDS))

    val postgreSQLContainer = KPostgreSQLContainer(
        "postgres:latest"
    )
        .withDatabaseName("restaurant")
        .withUsername("demouser")
        .withPassword("thepassword")
        .withStartupTimeout(Duration.of(60L, ChronoUnit.SECONDS))


    override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
        postgreSQLContainer.start()
        axonServerTestContainer.start()
        TestPropertyValues.of(
            "spring.datasource.url=" + postgreSQLContainer.jdbcUrl,
            "spring.datasource.username=" + postgreSQLContainer.username,
            "spring.datasource.password=" + postgreSQLContainer.password,
            "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect",
            "axon.axonserver.servers=" + axonServerTestContainer.getURL()
        ).applyTo(configurableApplicationContext)
    }
}
