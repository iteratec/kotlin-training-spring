package de.iteratec.solution.pizza

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [PizzaEndpoint::class])
class PizzaEndpointMockkTest {

    @TestConfiguration
    class TestConfig {
        @Bean
        fun pizzaService(): PizzaService = mockk(relaxUnitFun = true)
    }

    @Autowired
    private lateinit var pizzaService: PizzaService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `should return all pizzas`() {
        every { pizzaService.getAll() } returns listOf(Pizza(name = "Mokka", price = 42))

        mvc.perform(get("/pizza"))
            .andExpect(status().is2xxSuccessful)
            .andExpect(content().json("""[{"name": "Mokka", "price": 42}]"""))

        verify { pizzaService.getAll() }
    }
}
