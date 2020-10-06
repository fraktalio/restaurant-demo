package com.fraktalio.restaurant.command

import com.fraktalio.restaurant.command.api.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.messaging.annotation.MetaDataValue
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateLifecycle.createNew
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import java.util.stream.Collectors


@Aggregate(snapshotTriggerDefinition = "restaurantSnapshotTriggerDefinition")
internal class Restaurant {

    @AggregateIdentifier
    private lateinit var id: RestaurantId
    private lateinit var name: String

    @AggregateMember
    private lateinit var menu: RestaurantMenu
    private lateinit var state: RestaurantState

    private constructor()

    @CommandHandler
    constructor(command: CreateRestaurantCommand, @MetaDataValue(value = "auditEntry") auditEntry: AuditEntry) {
        apply(RestaurantCreatedEvent(command.name, command.menu, command.targetAggregateIdentifier, auditEntry))
    }

    @EventSourcingHandler
    fun on(event: RestaurantCreatedEvent) {
        id = event.aggregateIdentifier
        name = event.name
        menu = RestaurantMenu(
            event.menu.menuId,
            event.menu.menuItems.map { MenuItem(it.id, it.name, it.price) },
            event.menu.cuisine
        )
        state = RestaurantState.OPEN
    }

    @CommandHandler
    fun handle(command: PlaceRestaurantOrderCommand, @MetaDataValue(value = "auditEntry") auditEntry: AuditEntry) {
        if (menu.state == RestaurantMenuState.PASSIVE) {
            apply(
                RestaurantOrderRejectedEvent(
                    command.restaurantOrderId,
                    auditEntry,
                    "Menu is not activated"
                )
            )
        } else if (menu.items.stream().map { mi -> mi.id }.collect(Collectors.toList()).containsAll(
                command.orderDetails.lineItems.stream().map { li -> li.menuItemId }.collect(
                    Collectors.toList()
                )
            )
        ) {
            createNew(RestaurantOrder::class.java) { RestaurantOrder(command, auditEntry) }
        } else {
            apply(
                RestaurantOrderRejectedEvent(
                    command.restaurantOrderId,
                    auditEntry,
                    "Restaurant order invalid - not on the Menu"
                )
            )
        }
    }

    @CommandHandler
    fun handle(command: ChangeRestaurantMenuCommand, @MetaDataValue(value = "auditEntry") auditEntry: AuditEntry) {

        apply(
            RestaurantMenuChangedEvent(
                RestaurantMenuVO(command.menu.menuItems, this.menu.menuId),
                command.targetAggregateIdentifier,
                auditEntry
            )
        )
    }

    @EventSourcingHandler
    fun on(event: RestaurantMenuChangedEvent) {
        menu = RestaurantMenu(
            event.menu.menuId,
            event.menu.menuItems.map { MenuItem(it.id, it.name, it.price) },
            event.menu.cuisine
        )
    }
}
