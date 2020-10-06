package com.fraktalio.restaurant.query

import com.fraktalio.restaurant.command.api.AuditEntry
import com.fraktalio.restaurant.command.api.RestaurantCreatedEvent
import com.fraktalio.restaurant.command.api.RestaurantMenuChangedEvent
import com.fraktalio.restaurant.query.api.FindAllRestaurantsQuery
import com.fraktalio.restaurant.query.api.FindRestaurantQuery
import com.fraktalio.restaurant.query.api.MenuItemModel
import com.fraktalio.restaurant.query.api.RestaurantModel
import com.fraktalio.restaurant.query.repository.RestaurantRepository
import com.fraktalio.restaurant.query.repository.model.MenuItemEmbedable
import com.fraktalio.restaurant.query.repository.model.RestaurantEntity
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.messaging.annotation.MetaDataValue
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("restaurant-management")
internal class RestaurantHandler(
    private val repository: RestaurantRepository,
    private val queryUpdateEmitter: QueryUpdateEmitter
) {

    private fun convert(menuItems: List<MenuItemEmbedable>): List<MenuItemModel> =
        menuItems.map { MenuItemModel(it.menuId, it.name, it.price) }

    private fun convert(record: RestaurantEntity): RestaurantModel =
        RestaurantModel(
            record.id,
            record.aggregateVersion,
            record.name,
            record.menuItems?.let { convert(it) }
        )

    @EventHandler
    fun handle(event: RestaurantCreatedEvent) {

        val menuItems = ArrayList<MenuItemEmbedable>()
        for (item in event.menu.menuItems) {
            val menuItem = MenuItemEmbedable(item.id, item.name, item.price.amount)
            menuItems.add(menuItem)
        }

        val record = RestaurantEntity(event.aggregateIdentifier.identifier.toString(), 0, event.name, menuItems)
        repository.save(record)

        /* sending it to subscription queries of type FindRestaurantQuery, but only if the restaurant id matches. */
        queryUpdateEmitter.emit(
            FindRestaurantQuery::class.java,
            { query -> query.restaurantId.identifier == event.aggregateIdentifier.identifier },
            convert(record)
        )
        queryUpdateEmitter.emit(
            FindAllRestaurantsQuery::class.java,
            { true },
            convert(record)
        )
    }

    @EventHandler
    fun handle(event: RestaurantMenuChangedEvent) {

        val record = repository.findById(event.aggregateIdentifier.identifier.toString())
            .orElseThrow { UnsupportedOperationException("Restaurant order with id '" + event.aggregateIdentifier + "' not found") }

        val menuItems = ArrayList<MenuItemEmbedable>()
        for (item in event.menu.menuItems) {
            val menuItem = MenuItemEmbedable(item.id, item.name, item.price.amount)
            menuItems.add(menuItem)
        }
        record.menuItems = menuItems

        repository.save(record)

        /* sending it to subscription queries of type FindRestaurantQuery, but only if the restaurant id matches. */
        queryUpdateEmitter.emit(
            FindRestaurantQuery::class.java,
            { query -> query.restaurantId.identifier == event.aggregateIdentifier.identifier },
            convert(record)
        )
        queryUpdateEmitter.emit(
            FindAllRestaurantsQuery::class.java,
            { true },
            convert(record)
        )
    }

    @ResetHandler
    fun onReset() = repository.deleteAll()

    @QueryHandler
    fun handle(
        query: FindRestaurantQuery,
        @MetaDataValue(value = "auditEntry") auditEntry: AuditEntry
    ): RestaurantModel =
        convert(
            repository.findById(query.restaurantId.identifier.toString())
                .orElseThrow { UnsupportedOperationException("Restaurant with id '" + query.restaurantId + "' not found") })

    @QueryHandler
    fun handle(
        query: FindAllRestaurantsQuery,
        @MetaDataValue(value = "auditEntry") auditEntry: AuditEntry
    ): List<RestaurantModel> = repository.findAll().map { convert(it) }

}
