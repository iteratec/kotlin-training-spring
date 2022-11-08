package de.iteratec.solution.pizza

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

data class Pizza(
    @field:NotBlank
    @field:Size(min = 3, max = 16)
    val name: String,

    @field:Positive
    val price: Int
)