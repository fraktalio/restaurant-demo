package com.fraktalio.restaurant.query

import com.fraktalio.restaurant.command.api.AuditEntry
import com.fraktalio.restaurant.command.api.RestaurantOrderPlacedEvent
import com.fraktalio.restaurant.command.api.RestaurantOrderPreparedEvent
import com.fraktalio.restaurant.query.api.*
import com.fraktalio.restaurant.query.repository.RestaurantOrderRepository
import com.fraktalio.restaurant.query.repository.RestaurantRepository
import com.fraktalio.restaurant.query.repository.model.MenuItemEmbedable
import com.fraktalio.restaurant.query.repository.model.RestaurantorderEntity
import com.fraktalio.restaurant.query.repository.model.RestaurantOrderItemEmbedable
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.messaging.annotation.MetaDataValue
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("restaurantorder-management")
internal class RestaurantOrderHandler(
    private val repository: RestaurantOrderRepository,
    private val restaurantRepository: RestaurantRepository,
    private val queryUpdateEmitter: QueryUpdateEmitter
) {

    private fun convert(record: RestaurantorderEntity): RestaurantOrderModel = RestaurantOrderModel(
        record.id,
        record.aggregateVersion,
        record.lineItems!!.map { RestaurantOrderItemModel(it.menuId, it.name, it.quantity) },
        record.restaurantId,
        record.state
    )

    private fun convert(menuItems: List<MenuItemEmbedable>): List<MenuItemModel> =
        menuItems.map { MenuItemModel(it.menuId, it.name, it.price) }


    @EventHandler
    fun handle(event: RestaurantOrderPlacedEvent) {
        val restaurantOrderItems = ArrayList<RestaurantOrderItemEmbedable>()
        for (item in event.lineItems) {
            val restaurantOrderItem = RestaurantOrderItemEmbedable(item.menuItemId, item.name, item.quantity)
            restaurantOrderItems.add(restaurantOrderItem)
        }
        val record = RestaurantorderEntity(
            event.restaurantOrderId.identifier.toString(),
            0,
            restaurantOrderItems,
            event.aggregateIdentifier.identifier.toString(),
            RestaurantOrderState.CREATED,
            event.auditEntry.who
        )
        repository.save(record)

        /* sending it to subscription queries of type FindRestaurantOrderQuery, but only if the restaurant order id matches. */
        queryUpdateEmitter.emit(
            FindRestaurantOrderQuery::class.java,
            { query -> query.restaurantOrderId.identifier == event.restaurantOrderId.identifier },
            convert(record)
        )
        queryUpdateEmitter.emit(
            FindAllRestaurantOrdersByUserQuery::class.java,
            { query -> query.user == record.customer },
            convert(record)
        )
        queryUpdateEmitter.emit(
            FindAllRestaurantOrdersQuery::class.java,
            { true },
            convert(record)
        )
    }

    @EventHandler
    fun handle(event: RestaurantOrderPreparedEvent) {
        val record = repository.findById(event.aggregateIdentifier.identifier.toString())
            .orElseThrow { UnsupportedOperationException("Restaurant order with id '" + event.aggregateIdentifier + "' not found") }
        record.state = RestaurantOrderState.PREPARED
        repository.save(record)

        /* sending it to subscription queries of type FindRestaurantOrderQuery, but only if the restaurant order id matches. */
        queryUpdateEmitter.emit(
            FindRestaurantOrderQuery::class.java,
            { query -> query.restaurantOrderId.identifier == event.aggregateIdentifier.identifier },
            convert(record)
        )
        queryUpdateEmitter.emit(
            FindAllRestaurantOrdersByUserQuery::class.java,
            { query -> query.user == record.customer },
            convert(record)
        )
        queryUpdateEmitter.emit(
            FindAllRestaurantOrdersQuery::class.java,
            { true },
            convert(record)
        )
    }

    @ResetHandler
    fun onReset() = repository.deleteAll()

    @QueryHandler
    fun handle(
        query: FindRestaurantOrderQuery,
        @MetaDataValue(value = "auditEntry") auditEntry: AuditEntry
    ): RestaurantOrderModel =
        convert(
            repository.findById(query.restaurantOrderId.identifier.toString())
                .orElseThrow { UnsupportedOperationException("Restaurant order with id '" + query.restaurantOrderId + "' not found") })

    @QueryHandler
    fun handle(
        query: FindAllRestaurantOrdersQuery,
        @MetaDataValue(value = "auditEntry") auditEntry: AuditEntry
    ): List<RestaurantOrderModel> =
        repository.findAll().map { convert(it) }

    @QueryHandler
    fun handle(
        query: FindAllRestaurantOrdersByUserQuery,
        @MetaDataValue(value = "auditEntry") auditEntry: AuditEntry
    ): List<RestaurantOrderModel> =
        repository.findByCustomer(auditEntry.who).map { convert(it) }

}
