package de.iteratec.solution.pizza

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.datetime.Instant
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.ClassPathResource
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

interface PizzaRepository {
    fun findAll(): List<Pizza>
    fun findByName(name: String): Pizza?
    fun count(): Int
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

    override fun count(): Int = pizzaList.size

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

    override fun count(): Int = pizzaList.size

    override fun create(pizza: Pizza) {
        pizzaList.add(pizza)
    }
}

@Repository
@Qualifier("jdbcPizzaRepo")
@ConditionalOnProperty(name = ["pizza.repository.type"], havingValue = "jdbc")
class JdbcPizzaRepository(dataSource: DataSource) : PizzaRepository {

    private val jdbcTemplate: JdbcTemplate = JdbcTemplate(dataSource)

    override fun findAll(): List<Pizza> {
        return jdbcTemplate.query("select * from pizza", PizzaRowMapper())
    }

    override fun findByName(name: String): Pizza? = try {
        jdbcTemplate.queryForObject("select * from pizza where name = ?", PizzaRowMapper(), name)
    } catch (ex: EmptyResultDataAccessException) {
        null
    }

    override fun count(): Int = jdbcTemplate.queryForObject("select count(*) from pizza", Int::class.java) ?: 0

    override fun create(pizza: Pizza) {
        SimpleJdbcInsert(jdbcTemplate)
            .apply { withTableName("pizza") }
            .run { execute(pizza.toColumnMap()) }
    }

    private class PizzaRowMapper : RowMapper<Pizza> {
        override fun mapRow(rs: ResultSet, rowNum: Int): Pizza = Pizza(
            name = rs.getString("name"),
            price = rs.getInt("price"),
            vegan = rs.getBoolean("vegan"),
            createdOn = Instant.fromEpochMilliseconds(rs.getDate("created_on").time)
        )
    }

    private fun Pizza.toColumnMap() = mapOf(
        "name" to name,
        "price" to price,
        "vegan" to vegan,
        "created_on" to Date(createdOn.toEpochMilliseconds())
    )
}
