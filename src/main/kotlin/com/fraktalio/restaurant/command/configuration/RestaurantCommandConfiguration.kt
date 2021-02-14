package com.fraktalio.restaurant.command.configuration

import com.fraktalio.restaurant.web.configuration.Logging
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.DuplicateCommandHandlerResolution
import org.axonframework.commandhandling.DuplicateCommandHandlerResolver
import org.axonframework.common.caching.Cache
import org.axonframework.common.caching.WeakReferenceCache
import org.axonframework.config.EventProcessingConfigurer
import org.axonframework.eventhandling.EventBus
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.axonframework.messaging.interceptors.LoggingInterceptor
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors

@Configuration
internal class RestaurantCommandConfiguration :
    Logging {

    /************************************************/
    /* Register interceptors on the bus */
    /************************************************/

    @Autowired
    fun registerCommandInterceptorsOnBus(commandBus: CommandBus) {
        commandBus.registerHandlerInterceptor(LoggingInterceptor())
    }

    @Autowired
    fun registerEventInterceptors(eventBus: EventBus) {
        eventBus.registerDispatchInterceptor(LoggingInterceptor())
    }

    @Autowired
    fun configure(config: EventProcessingConfigurer) {
        config.registerDefaultHandlerInterceptor { t, u -> LoggingInterceptor(u) }
    }

    /***************************************/
    /*  Aggregate cache configuration      */
    /***************************************/

    @Bean("cache")
    fun cache(): Cache = WeakReferenceCache()

    /***************************************/
    /*  Aggregate snapshot configuration   */
    /***************************************/

    @Bean
    fun snapshotter(): SpringAggregateSnapshotterFactoryBean {
        val snapshotter = SpringAggregateSnapshotterFactoryBean()
        snapshotter.setExecutor(Executors.newSingleThreadExecutor())
        return snapshotter
    }

    @Bean("restaurantSnapshotTriggerDefinition")
    fun restaurantSnapshotTriggerDefinition(snapshotter: Snapshotter, restaurantProperties: RestaurantProperties) =
        EventCountSnapshotTriggerDefinition(snapshotter, restaurantProperties.snapshotTriggerTresholdRestaurant)

    @Bean("restaurantOrderSnapshotTriggerDefinition")
    fun restaurantOrderSnapshotTriggerDefinition(
        snapshotter: Snapshotter,
        restaurantProperties: RestaurantProperties
    ) =
        EventCountSnapshotTriggerDefinition(
            snapshotter,
            restaurantProperties.snapshotTriggerTresholdRestaurantOrder
        )

    /***************************************************************************/
    /* Duplicate command handler configured to fail on duplicate registrations */

    /* Command is always routed to a single destination.
    This means that during the registration of a command handler within a given JVM,
    a second registration of an identical command handler method should be dealt with in a desirable manner.

    By default, the LoggingDuplicateCommandHandlerResolver is used, which will log a warning and returns the candidate handler.*/

    /***************************************************************************/
    @Bean
    fun duplicateCommandHandlerResolver(): DuplicateCommandHandlerResolver =
        DuplicateCommandHandlerResolution.rejectDuplicates()
}
