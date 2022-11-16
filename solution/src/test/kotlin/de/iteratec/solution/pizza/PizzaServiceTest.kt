package de.iteratec.solution.pizza

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PizzaServiceTest {

    @Autowired
    lateinit var pizzaService: PizzaService

    @Test
    fun `getAll should return all pizzas`() {
        val pizzas = pizzaService.getAll()
        Assertions.assertThat(pizzas).hasSize(4)
        Assertions.assertThat(pizzas).contains(Pizza(name = "Jsonini", price = 10))
    }
}
