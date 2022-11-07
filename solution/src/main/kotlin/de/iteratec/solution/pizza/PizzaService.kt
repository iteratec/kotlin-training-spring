package de.iteratec.solution.pizza

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class PizzaService(@Qualifier("jsonPizzaRepo") private val pizzaRepository: PizzaRepository) {

    fun printAll() {
        println("Menu:")
        pizzaRepository.findAll().forEach { println(" * ${it.name} (${it.price} €)") }
    }
}