package de.iteratec.solution.pizza

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import java.net.URL

interface PizzaRepository {
    fun findAll(): List<Pizza>
}

@Repository
class InMemoryPizzaRepository : PizzaRepository {

    override fun findAll(): List<Pizza> = listOf(
        Pizza(name = "Capricciosa", price = 12),
        Pizza(name = "Calzone", price = 8),
        Pizza(name = "Regina", price = 10)
    )
}

@Repository
@Qualifier("jsonPizzaRepo")
class JsonPizzaRepository(private val objectMapper: ObjectMapper) : PizzaRepository {

    override fun findAll(): List<Pizza> =
        objectMapper.readValue(URL("classpath:/pizza-list.json"))
}