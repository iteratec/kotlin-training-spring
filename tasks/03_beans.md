# Spring beans tasks

## In-memory pizza
We will build a simple in-memory pizza store 🍕 

1. Create a new package `pizza` and place a data class `Pizza` inside. `Pizza` should have a name and an
integer price.
2. Create an interface `PizzaRepository` containing one method `findAll()` that returns a list of pizzas. Then
create an in-memory implementation of `PizzaRepository` named `InMemoryPizzaRepository` that returns a hard-coded
list of pizzas. Mark your implementation as `@Repository`.
3. Create a `PizzaService` marked as `@Service` with a single dependency to `PizzaRepository`. Then implement
a single method `printAll`, that queries all pizzas from the repository and prints them to the standard output.
4. Add a dependency to the `PizzaService` to the main application class and call the `printAll` method from
the `run` method. Start the application and check if the pizza list is printed at application's startup.

## JSON pizza
Now we will read pizzas from a JSON file.

1. Create a new class `JsonConfig` in the package `config`. In `JsonConfig` create a bean
of type `ObjectMapper`. You can use the global method `jacksonObjectMapper()` to instantiate the mapper. `ObjectMapper`
is a class from Jackson, that allows you to read and write JSON data.
2. In the pizza repository file add a new repository implementation `JsonPizzaRepository` and mark it
as @Repository. It should have a dependency on the `ObjectMapper` and return parsed pizza list from the
file `pizza-list.json`. Use the  following snippet to read the pizzas:
`objectMapper.readValue(ClassPathResource("pizza-list.json").inputStream)`.
3. Try to start the application, it should report a context configuration error.
4. Use `@Qualifier` to resolve the unambiguity.
5. Restart the application and enjoy the pizzas loaded from JSON file.
