package com.fraktalio.restaurant.command.api

import java.io.Serializable
import java.util.*

abstract class AuditableAbstractEvent(open val auditEntry: AuditEntry) : Serializable

abstract class RestaurantEvent(open val aggregateIdentifier: RestaurantId, override val auditEntry: AuditEntry) :
    AuditableAbstractEvent(auditEntry)

abstract class RestaurantOrderEvent(
    open val aggregateIdentifier: RestaurantOrderId,
    override val auditEntry: AuditEntry
) : AuditableAbstractEvent(auditEntry)

data class RestaurantCreatedEvent(
    val name: String,
    val menu: RestaurantMenuVO,
    override val aggregateIdentifier: RestaurantId,
    override val auditEntry: AuditEntry
) : RestaurantEvent(aggregateIdentifier, auditEntry)

data class RestaurantMenuChangedEvent(
    val menu: RestaurantMenuVO,
    override val aggregateIdentifier: RestaurantId,
    override val auditEntry: AuditEntry
) : RestaurantEvent(aggregateIdentifier, auditEntry)

data class RestaurantMenuActivatedEvent(
    val menuId: UUID,
    override val aggregateIdentifier: RestaurantId,
    override val auditEntry: AuditEntry
) : RestaurantEvent(aggregateIdentifier, auditEntry)

data class RestaurantMenuPassivatedEvent(
    val menuId: UUID,
    override val aggregateIdentifier: RestaurantId,
    override val auditEntry: AuditEntry
) : RestaurantEvent(aggregateIdentifier, auditEntry)

data class RestaurantOrderPlacedEvent(
    val lineItems: List<RestaurantOrderLineItem>,
    val restaurantOrderId: RestaurantOrderId,
    override val aggregateIdentifier: RestaurantId,
    override val auditEntry: AuditEntry
) : RestaurantEvent(aggregateIdentifier, auditEntry)

data class RestaurantOrderPreparedEvent(
    override val aggregateIdentifier: RestaurantOrderId,
    override val auditEntry: AuditEntry
) : RestaurantOrderEvent(aggregateIdentifier, auditEntry)

data class RestaurantOrderRejectedEvent(
    override val aggregateIdentifier: RestaurantOrderId,
    override val auditEntry: AuditEntry,
    val reason: String
) : RestaurantOrderEvent(aggregateIdentifier, auditEntry)
