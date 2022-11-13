package de.iteratec.solution.pizza

import de.iteratec.solution.config.WebSecurityConfig
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [PizzaEndpoint::class])
//@Import(WebSecurityConfig::class)
class PizzaEndpointTest {

    @MockBean
    private lateinit var pizzaService: PizzaService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    @WithMockUser("test")
    fun nonEmpty() {
        mvc.perform(get("/pizza")).andExpect(status().is2xxSuccessful)
        org.mockito.Mockito.verify(pizzaService).getAll()
    }
}

@WebMvcTest(controllers = [PizzaEndpoint::class])
@Import(WebSecurityConfig::class)
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

    @BeforeEach
    fun beforeEach() {
        every { pizzaService.getAll() } returns listOf()
    }

    @Test
    @WithMockUser("test")
    fun nonEmpty() {
        mvc.perform(get("/pizza"))
            .andExpect(status().is2xxSuccessful)
            .andExpect(content().json("[]"))

        verify { pizzaService.getAll() }
    }
}
