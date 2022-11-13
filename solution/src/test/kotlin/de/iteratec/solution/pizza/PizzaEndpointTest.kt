package de.iteratec.solution.pizza

import de.iteratec.solution.config.WebSecurityConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [PizzaEndpoint::class])
@Import(WebSecurityConfig::class)
class PizzaEndpointTest {

    @MockBean
    private lateinit var pizzaService: PizzaService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun nonEmpty() {
        mvc.perform(get("/pizza").header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA=="))
            .andExpect(status().is2xxSuccessful)
    }
}