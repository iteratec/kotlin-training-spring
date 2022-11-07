package de.iteratec.solution

import de.iteratec.solution.pizza.PizzaService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
// Uncomment to disable automatic persistence configuration
// exclude = [DataSourceAutoConfiguration::class]
)
class SolutionApplication(private val pizzaService: PizzaService) : CommandLineRunner {

    override fun run(vararg args: String?) {
        pizzaService.printAll()
    }
}

fun main(args: Array<String>) {
    runApplication<SolutionApplication>(*args)
}
