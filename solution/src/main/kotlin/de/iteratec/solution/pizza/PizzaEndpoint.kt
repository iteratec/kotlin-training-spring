package de.iteratec.solution.pizza

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/pizza")
class PizzaEndpoint(private val pizzaService: PizzaService) {

    @GetMapping
    fun getAll() = pizzaService.getAll()

    @GetMapping("/image", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getImage(@RequestParam(name = "name") name: String): ByteArray = pizzaService.getImage(name)

    @PostMapping
    fun create(@Validated @RequestBody pizza: Pizza) {
        pizzaService.create(pizza)
    }

    @ExceptionHandler(IllegalPizzaNameException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun illegalPizzaNameException() {
        // Nothing to do
    }
}
