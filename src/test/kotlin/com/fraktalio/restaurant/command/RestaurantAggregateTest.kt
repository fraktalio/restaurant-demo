package com.fraktalio.restaurant.command

import com.fraktalio.api.AuditEntry
import com.fraktalio.restaurant.command.api.*
import com.fraktalio.restaurant.web.configuration.LoggingReactorMessageDispatchInterceptor
import com.fraktalio.restaurant.web.configuration.SpringSecurityReactorMessageDispatchInterceptor
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class RestaurantAggregateTest {

    private lateinit var fixture: FixtureConfiguration<Restaurant>
    private val auditEntry: AuditEntry =
        AuditEntry()
    private val orderId: RestaurantOrderId = RestaurantOrderId()
    private val restaurantId: RestaurantId = RestaurantId()
    private val lineItem: RestaurantOrderLineItem = RestaurantOrderLineItem(1, "menuItemId", "name")
    private val lineItems: MutableList<RestaurantOrderLineItem> = ArrayList()
    private val orderDetails: RestaurantOrderDetails = RestaurantOrderDetails(lineItems)


    @BeforeEach
    fun setUp() {
        fixture = AggregateTestFixture(Restaurant::class.java)
        fixture.registerCommandDispatchInterceptor(SpringSecurityReactorMessageDispatchInterceptor())
        fixture.registerCommandDispatchInterceptor(LoggingReactorMessageDispatchInterceptor())

        lineItems.add(lineItem)
    }

    @Test
    fun `create restaurant`() {
        val name = "Fancy"
        val menuItems = ArrayList<MenuItemVO>()
        val item = MenuItemVO("id", "name", Money(BigDecimal.valueOf(100)))
        menuItems.add(item)
        val menu = RestaurantMenuVO(menuItems)
        val createRestaurantCommand = CreateRestaurantCommand(name, menu)
        val restaurantCreatedEvent =
            RestaurantCreatedEvent(name, menu, createRestaurantCommand.targetAggregateIdentifier, auditEntry)

        fixture
            .given()
            .`when`(createRestaurantCommand)
            .expectEvents(restaurantCreatedEvent)
    }

    @Test
    fun `activate restaurant menu`() {
        val name = "Fancy"
        val menuItems = ArrayList<MenuItemVO>()
        val item = MenuItemVO("menuItemId", "name", Money(BigDecimal.valueOf(100)))
        menuItems.add(item)
        val menu = RestaurantMenuVO(menuItems)
        val restaurantCreatedEvent = RestaurantCreatedEvent(name, menu, restaurantId, auditEntry)

        val activateRestaurantMenuCommand =
            ActivateRestaurantMenuCommand(
                menuId = restaurantCreatedEvent.menu.menuId,
                targetAggregateIdentifier = restaurantId
            )
        val restaurantMenuActivatedEvent =
            RestaurantMenuActivatedEvent(restaurantCreatedEvent.menu.menuId, restaurantId, auditEntry)

        fixture
            .given(restaurantCreatedEvent)
            .`when`(activateRestaurantMenuCommand)
            .expectEvents(restaurantMenuActivatedEvent)
    }


    @Test
    fun `add new - replace restaurant menu`() {
        val name = "Fancy"
        val menuItems = ArrayList<MenuItemVO>()
        val item = MenuItemVO("menuItemId", "name", Money(BigDecimal.valueOf(150)))
        menuItems.add(item)
        val menu = RestaurantMenuVO(menuItems)
        val restaurantCreatedEvent = RestaurantCreatedEvent(name, menu, restaurantId, auditEntry)


        val menuItems2 = ArrayList<MenuItemVO>()
        val item2 = MenuItemVO("menuItemId2", "name2", Money(BigDecimal.valueOf(170)))
        menuItems.add(item2)
        val menu2 = RestaurantMenuVO(menuItems2, menu.menuId)
        val changeRestaurantMenuCommand =
            ChangeRestaurantMenuCommand(menu2, restaurantId)
        val restaurantMenuChangedEvent =
            RestaurantMenuChangedEvent(menu2, restaurantId, auditEntry)

        fixture
            .given(restaurantCreatedEvent)
            .`when`(changeRestaurantMenuCommand)
            .expectEvents(restaurantMenuChangedEvent)
    }

    @Test
    fun `pasivate restaurant menu`() {
        val name = "Fancy"
        val menuItems = ArrayList<MenuItemVO>()
        val item = MenuItemVO("menuItemId", "name", Money(BigDecimal.valueOf(100)))
        menuItems.add(item)
        val menu = RestaurantMenuVO(menuItems)
        val restaurantCreatedEvent = RestaurantCreatedEvent(name, menu, restaurantId, auditEntry)

        val passivateRestaurantMenuCommand =
            PassivateRestaurantMenuCommand(restaurantCreatedEvent.menu.menuId, restaurantId)
        val restaurantMenuPassivatedEvent =
            RestaurantMenuPassivatedEvent(restaurantCreatedEvent.menu.menuId, restaurantId, auditEntry)

        fixture
            .given(restaurantCreatedEvent)
            .`when`(passivateRestaurantMenuCommand)
            .expectEvents(restaurantMenuPassivatedEvent)
    }

    @Test
    fun `place restaurant order`() {
        val name = "Fancy"
        val menuItems = ArrayList<MenuItemVO>()
        val item = MenuItemVO("menuItemId", "name", Money(BigDecimal.valueOf(100)))
        menuItems.add(item)
        val menu = RestaurantMenuVO(menuItems)
        val restaurantCreatedEvent = RestaurantCreatedEvent(name, menu, restaurantId, auditEntry)

        val placeRestaurantOrderCommand = PlaceRestaurantOrderCommand(restaurantId, orderDetails, orderId)
        val restaurantOrderPlacedEvent = RestaurantOrderPlacedEvent(lineItems, orderId, restaurantId, auditEntry)

        fixture
            .given(restaurantCreatedEvent)
            .`when`(placeRestaurantOrderCommand)
            .expectEvents(restaurantOrderPlacedEvent)
    }

    @Test
    fun `reject restaurant order`() {
        val name = "Fancy"
        val menuItems = ArrayList<MenuItemVO>()
        val item = MenuItemVO("WRONG", "name", Money(BigDecimal.valueOf(100)))
        menuItems.add(item)
        val menu = RestaurantMenuVO(menuItems)
        val restaurantCreatedEvent = RestaurantCreatedEvent(name, menu, restaurantId, auditEntry)

        val createRestaurantOrderCommand = PlaceRestaurantOrderCommand(restaurantId, orderDetails, orderId)
        val restaurantOrderRejectedEvent =
            RestaurantOrderRejectedEvent(orderId, auditEntry, "Restaurant order invalid - not on the Menu")

        fixture
            .given(restaurantCreatedEvent)
            .`when`(createRestaurantOrderCommand)
            .expectEvents(restaurantOrderRejectedEvent)
    }

}
