package de.iteratec.solution.pizza

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Repository

interface PizzaRepository {
    fun findAll(): List<Pizza>
    fun findByName(name: String): Pizza?
    fun create(pizza: Pizza)
}

@Repository
@ConditionalOnProperty(name = ["pizza.repository.type"], havingValue = "in-memory")
class InMemoryPizzaRepository : PizzaRepository {

    private val pizzaList: MutableList<Pizza> = mutableListOf(
        Pizza(name = "Capricciosa", price = 12),
        Pizza(name = "Calzone", price = 8),
        Pizza(name = "Regina", price = 10),
        Pizza(name = "In-Memoritta", price = 10),
    )

    override fun findAll(): List<Pizza> = pizzaList

    override fun findByName(name: String): Pizza? = pizzaList.find { it.name == name }

    override fun create(pizza: Pizza) {
        pizzaList.add(pizza)
    }
}

@Repository
@Qualifier("jsonPizzaRepo")
@ConditionalOnProperty(name = ["pizza.repository.type"], havingValue = "json")
class JsonPizzaRepository(objectMapper: ObjectMapper) : PizzaRepository {

    private val pizzaList: MutableList<Pizza> = mutableListOf<Pizza>().apply {
        addAll(objectMapper.readValue(ClassPathResource("pizza-list.json").inputStream))
    }

    override fun findAll(): List<Pizza> = pizzaList

    override fun findByName(name: String): Pizza? = pizzaList.find { it.name == name }

    override fun create(pizza: Pizza) {
        pizzaList.add(pizza)
    }
}