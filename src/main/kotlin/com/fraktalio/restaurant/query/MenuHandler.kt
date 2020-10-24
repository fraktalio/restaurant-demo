package com.fraktalio.restaurant.query

import com.fraktalio.api.AuditEntry
import com.fraktalio.restaurant.command.api.*
import com.fraktalio.restaurant.query.api.FindAllMenusQuery
import com.fraktalio.restaurant.query.api.FindMenuQuery
import com.fraktalio.restaurant.query.api.MenuItemModel
import com.fraktalio.restaurant.query.api.MenuModel
import com.fraktalio.restaurant.query.repository.MenuRepository
import com.fraktalio.restaurant.query.repository.model.MenuEntity
import com.fraktalio.restaurant.query.repository.model.MenuItemEmbedable
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.annotation.MetaDataValue
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("menu-management")
internal class MenuHandler(
    private val repository: MenuRepository,
    private val queryUpdateEmitter: QueryUpdateEmitter
) {

    private fun convert(menuEntity: MenuEntity): MenuModel =
        MenuModel(
            menuEntity.id,
            menuEntity.menuVersion.toString(),
            menuEntity.restaurantId,
            menuEntity.active,
            menuEntity.menuItems.map { MenuItemModel(it.menuId, it.name, it.price) })

    @EventHandler
    fun handle(event: RestaurantCreatedEvent) {
        val saved: MenuEntity = repository.save(
            MenuEntity(
                event.menu.menuId.toString(),
                1,
                event.aggregateIdentifier.identifier.toString(),
                true,
                event.menu.menuItems.map { MenuItemEmbedable(it.id, it.name, it.price.amount) })
        )
        queryUpdateEmitter.emit(
            FindMenuQuery::class.java,
            { query -> query.menuId == event.menu.menuId },
            convert(saved)
        )
        queryUpdateEmitter.emit(
            FindAllMenusQuery::class.java,
            { true },
            convert(saved)
        )
    }

    @EventHandler
    fun handle(event: RestaurantMenuActivatedEvent) {
        val record = repository.findById(event.menuId.toString())
            .orElseThrow { UnsupportedOperationException("Restaurant menu with id '" + event.menuId + "' not found") }
        record.active = true
        record.menuVersion = record.menuVersion + 1
        repository.save(record)
        queryUpdateEmitter.emit(
            FindMenuQuery::class.java,
            { query -> query.menuId == event.menuId },
            convert(record)
        )
        queryUpdateEmitter.emit(
            FindAllMenusQuery::class.java,
            { true },
            convert(record)
        )
    }

    @EventHandler
    fun handle(event: RestaurantMenuChangedEvent) {
        val record = repository.findById(event.menu.menuId.toString())
            .orElseThrow { UnsupportedOperationException("Restaurant menu with id '" + event.menu.menuId + "' not found") }
        record.active = true
        record.menuVersion = record.menuVersion + 1
        repository.save(record)
        queryUpdateEmitter.emit(
            FindMenuQuery::class.java,
            { query -> query.menuId == event.menu.menuId },
            convert(record)
        )
        queryUpdateEmitter.emit(
            FindAllMenusQuery::class.java,
            { true },
            convert(record)
        )
    }

    @EventHandler
    fun handle(event: RestaurantMenuPassivatedEvent) {
        val record = repository.findById(event.menuId.toString())
            .orElseThrow { UnsupportedOperationException("Restaurant menu with id '" + event.menuId + "' not found") }
        record.active = false
        record.menuVersion = record.menuVersion + 1
        repository.save(record)
        queryUpdateEmitter.emit(
            FindMenuQuery::class.java,
            { query -> query.menuId == event.menuId },
            convert(record)
        )
        queryUpdateEmitter.emit(
            FindAllMenusQuery::class.java,
            { true },
            convert(record)
        )
    }

    @QueryHandler
    fun handle(query: FindMenuQuery, @MetaDataValue(value = "auditEntry") auditEntry: AuditEntry): MenuModel =
        convert(repository.findById(query.menuId.toString()).orElseThrow())

    @QueryHandler
    fun handle(query: FindAllMenusQuery, @MetaDataValue(value = "auditEntry") auditEntry: AuditEntry): List<MenuModel> =
        repository.findAll().map { convert(it) }


}
