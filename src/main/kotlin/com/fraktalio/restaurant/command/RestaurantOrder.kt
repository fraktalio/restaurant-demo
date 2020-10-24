package com.fraktalio.restaurant.command

import com.fraktalio.api.AuditEntry
import com.fraktalio.restaurant.command.api.*
import org.axonframework.commandhandling.CommandExecutionException
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.messaging.annotation.MetaDataValue
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate

@Aggregate(snapshotTriggerDefinition = "restaurantOrderSnapshotTriggerDefinition")
internal class RestaurantOrder {

    @AggregateIdentifier
    private lateinit var id: RestaurantOrderId
    private lateinit var restaurantId: RestaurantId
    private lateinit var state: RestaurantOrderState
    private lateinit var lineItems: List<RestaurantOrderLineItem>

    private constructor()

    constructor(command: PlaceRestaurantOrderCommand, auditEntry: AuditEntry) {
        apply(
            RestaurantOrderPlacedEvent(
                command.orderDetails.lineItems,
                command.restaurantOrderId,
                command.targetAggregateIdentifier,
                auditEntry
            )
        )
    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderPlacedEvent) {
        id = event.restaurantOrderId
        restaurantId = event.aggregateIdentifier
        lineItems = event.lineItems
        state = RestaurantOrderState.CREATED
    }

    @CommandHandler
    fun handle(
        command: MarkRestaurantOrderAsPreparedCommand,
        @MetaDataValue(value = "auditEntry") auditEntry: AuditEntry
    ) {
        if (RestaurantOrderState.CREATED == state) {
            apply(RestaurantOrderPreparedEvent(command.targetAggregateIdentifier, auditEntry))
        } else {
            throw CommandExecutionException(
                "UnsupportedOperationException",
                UnsupportedOperationException("The current state is not CREATED"),
                command
            )

        }
    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderPreparedEvent) {
        state = RestaurantOrderState.PREPARED
    }
}
