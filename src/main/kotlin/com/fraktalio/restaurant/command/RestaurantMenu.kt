package com.fraktalio.restaurant.command

import com.fraktalio.api.AuditEntry
import com.fraktalio.restaurant.command.api.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.messaging.annotation.MetaDataValue
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.EntityId
import java.util.*


internal class MenuItem(val id: String, val name: String, val price: Money) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MenuItem

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

internal enum class RestaurantMenuState {
    ACTIVE,
    PASSIVE
}

enum class RestaurantMenuCuisine {
    SERBIAN,
    ITALIAN,
    INDIAN,
    TURKISH,
    GENERAL

}

internal class RestaurantMenu(
    @EntityId val menuId: UUID,
    val items: List<MenuItem>,
    val cuisine: RestaurantMenuCuisine,
    var state: RestaurantMenuState = RestaurantMenuState.ACTIVE
) {

    @CommandHandler
    fun handle(command: ActivateRestaurantMenuCommand, @MetaDataValue(value = "auditEntry") auditEntry: AuditEntry) {
        if (menuId == command.menuId)
            apply(RestaurantMenuActivatedEvent(command.menuId, command.targetAggregateIdentifier, auditEntry))
    }

    @CommandHandler
    fun handle(command: PassivateRestaurantMenuCommand, @MetaDataValue(value = "auditEntry") auditEntry: AuditEntry) {
        if (menuId == command.menuId)
            apply(RestaurantMenuPassivatedEvent(command.menuId, command.targetAggregateIdentifier, auditEntry))

    }

    @EventSourcingHandler
    fun on(event: RestaurantMenuActivatedEvent) {
        state = RestaurantMenuState.ACTIVE
    }

    @EventSourcingHandler
    fun on(event: RestaurantMenuPassivatedEvent) {
        state = RestaurantMenuState.PASSIVE
    }

}
