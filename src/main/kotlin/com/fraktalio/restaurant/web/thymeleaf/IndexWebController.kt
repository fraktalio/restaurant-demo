package com.fraktalio.restaurant.web.thymeleaf

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import reactor.core.publisher.Mono


@Controller
internal class IndexWebController {

    @PreAuthorize("hasRole('MANAGER') or hasRole('USER')")
    @GetMapping("/")
    fun restaurants(model: Model, @AuthenticationPrincipal user: UserDetails): Mono<String> {
        return if (user.authorities.map { it.authority }
                .contains("ROLE_MANAGER")) Mono.just("index-manager") else Mono.just("index-user")
    }

}


