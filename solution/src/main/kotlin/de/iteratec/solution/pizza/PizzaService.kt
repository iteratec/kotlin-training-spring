package de.iteratec.solution.pizza

import org.springframework.stereotype.Service

@Service
class PizzaService(
    // Optionally add qualifier to resolve unambiguities: @Qualifier("jsonPizzaRepo")
    private val pizzaRepository: PizzaRepository,
    private val menuProps: MenuProperties
) {

    fun printAll() {
        println("*** Menu ${menuProps.name} v${menuProps.version}")
        println("***   created on ${menuProps.createdOn}")
        pizzaRepository.findAll().forEach { println(" * ${it.name} (${it.price} €)") }
    }
}