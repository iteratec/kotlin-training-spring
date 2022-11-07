package de.iteratec.solution.pizza

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "pizza.menu")
data class MenuProperties(
    val name: String,
    val version: Int = 99,
    val createdOn: String
)