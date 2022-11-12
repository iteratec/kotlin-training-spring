package de.iteratec.solution.user

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserEndpoint {

    @GetMapping("/current")
    fun current(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication?.name ?: "unauthenticated"
    }
}