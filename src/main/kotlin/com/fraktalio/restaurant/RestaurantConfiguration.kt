package com.fraktalio.restaurant

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
class RestaurantConfiguration {

    // https://github.com/AxonFramework/AxonFramework/issues/1418
    @Autowired
    fun configureObjectMapper(mapper: ObjectMapper) {
        mapper.activateDefaultTyping(
            mapper.polymorphicTypeValidator,
            ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE
        )
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    }
}
