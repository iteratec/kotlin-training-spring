package de.iteratec.solution.pizza

import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.lang.RuntimeException

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

    fun getAll(): List<Pizza> = pizzaRepository.findAll()

    fun getImage(name: String): ByteArray {
        if (pizzaRepository.findByName(name) == null) {
            throw IllegalPizzaNameException()
        }

        return ClassPathResource("pizza.jpeg").inputStream.readAllBytes()
    }

    fun count(): Int = pizzaRepository.count()

    fun create(pizza: Pizza) {
        pizzaRepository.create(pizza)
    }
}

class IllegalPizzaNameException: RuntimeException()
