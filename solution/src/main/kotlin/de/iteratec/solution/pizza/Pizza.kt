package de.iteratec.solution.pizza

import kotlinx.datetime.Instant
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

@Serializable
data class Pizza(
    @field:NotBlank
    @field:Size(min = 3, max = 16)
    val name: String,

    @field:Positive
    val price: Int,

    @EncodeDefault
    @SerialName("veganFriendly")
    val vegan: Boolean = false,

    @EncodeDefault
    val createdOn: Instant = Instant.parse("2022-04-07T12:42:00Z")
)