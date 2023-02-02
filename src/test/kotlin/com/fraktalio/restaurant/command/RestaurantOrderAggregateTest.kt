package com.fraktalio.restaurant.command

import com.fraktalio.api.AuditEntry
import com.fraktalio.restaurant.command.api.*
import com.fraktalio.restaurant.web.configuration.LoggingReactorMessageDispatchInterceptor
import com.fraktalio.restaurant.web.configuration.SpringSecurityReactorMessageDispatchInterceptor
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RestaurantOrderAggregateTest {

    private lateinit var fixture: FixtureConfiguration<RestaurantOrder>
    private val orderId: RestaurantOrderId = RestaurantOrderId()
    private val restaurantId: RestaurantId = RestaurantId()
    private val lineItem: RestaurantOrderLineItem = RestaurantOrderLineItem(1, "menuItemId", "name")
    private val lineItems: MutableList<RestaurantOrderLineItem> = ArrayList()
    private val auditEntry: AuditEntry =
        AuditEntry()

    @BeforeEach
    fun setUp() {
        fixture = AggregateTestFixture(RestaurantOrder::class.java)
        fixture.registerCommandDispatchInterceptor(SpringSecurityReactorMessageDispatchInterceptor())
        fixture.registerCommandDispatchInterceptor(LoggingReactorMessageDispatchInterceptor())
        lineItems.add(lineItem)
    }

    @Test
    fun `mark order as prepared`() {
        val restaurantOrderPlacedEvent = RestaurantOrderPlacedEvent(lineItems, orderId, restaurantId, auditEntry)

        val markRestaurantOrderAsPreparedCommand = MarkRestaurantOrderAsPreparedCommand(orderId)
        val restaurantOrderPreparedEvent = RestaurantOrderPreparedEvent(orderId, auditEntry)

        fixture.given(restaurantOrderPlacedEvent).`when`(markRestaurantOrderAsPreparedCommand)
            .expectEvents(restaurantOrderPreparedEvent)
    }
}
