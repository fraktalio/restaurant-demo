package com.fraktalio.restaurant.web.configuration

import com.fraktalio.restaurant.command.api.ANONYMOUS
import com.fraktalio.restaurant.command.api.AuditEntry
import org.axonframework.extensions.reactor.messaging.ReactorMessageDispatchInterceptor
import org.axonframework.messaging.Message
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.core.userdetails.User
import reactor.core.publisher.Mono

@Suppress("UNCHECKED_CAST")
class SpringSecurityReactorMessageDispatchInterceptor<M : Message<*>> :
    ReactorMessageDispatchInterceptor<M> {
    override fun intercept(message: Mono<M>): Mono<M> {
        return ReactiveSecurityContextHolder.getContext().defaultIfEmpty(
            SecurityContextImpl(
                AnonymousAuthenticationToken(
                    ANONYMOUS,
                    ANONYMOUS,
                    listOf(
                        GrantedAuthority { ANONYMOUS })
                )
            )
        )
            .map { it.authentication }
            .flatMap { authentication ->
                message.map {
                    it.andMetaData(
                        mapOf(
                            Pair(
                                "auditEntry",
                                AuditEntry(
                                    who = if (authentication.principal is User) (authentication.principal as User).username else authentication.principal.toString(),
                                    authorities = authentication.authorities.map { i -> i.authority })
                            )
                        )
                    ) as M
                }
            }
    }
}
