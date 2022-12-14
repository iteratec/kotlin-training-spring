package de.iteratec.solution.pizza

import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(controllers = [PizzaEndpoint::class])
class PizzaEndpointTest {

    @MockBean
    private lateinit var pizzaService: PizzaService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `should return all pizzas`() {
        `when`(pizzaService.getAll()).thenReturn(
            listOf(
                Pizza(name = "Calzone", price = 1),
                Pizza(name = "Testone", price = 2),
            )
        )

        mvc.perform(get("/pizza"))
            .andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("$.[0].name").value("Calzone"))
            .andExpect(jsonPath("$.[1].name").value("Testone"))

        org.mockito.Mockito.verify(pizzaService).getAll()
    }
}
