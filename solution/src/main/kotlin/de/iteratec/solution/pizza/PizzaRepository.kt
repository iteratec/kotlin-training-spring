package de.iteratec.solution.pizza

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Repository
import java.net.URL

interface PizzaRepository {
    fun findAll(): List<Pizza>
}

@Repository
@ConditionalOnProperty(name = ["pizza.repository.type"], havingValue = "in-memory")
class InMemoryPizzaRepository : PizzaRepository {

    private val pizzaList: List<Pizza> = listOf(
        Pizza(name = "Capricciosa", price = 12),
        Pizza(name = "Calzone", price = 8),
        Pizza(name = "Regina", price = 10),
        Pizza(name = "In-Memoritta", price = 10),
    )

    override fun findAll(): List<Pizza> = pizzaList
}

@Repository
@Qualifier("jsonPizzaRepo")
@ConditionalOnProperty(name = ["pizza.repository.type"], havingValue = "json")
class JsonPizzaRepository(objectMapper: ObjectMapper) : PizzaRepository {

    private val pizzaList: List<Pizza> = objectMapper.readValue(URL("classpath:/pizza-list.json"))

    override fun findAll(): List<Pizza> = pizzaList
}