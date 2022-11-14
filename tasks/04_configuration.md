# Application configuration tasks

## Using @ConfigurationProperties
1. Enable configuration property scanning in the application by adding `@ConfigurationPropertiesScan` to the main 
application class.
2. In the `pizza` package create a data class `MenuProperties`, which will contain basic meta-data of our pizza menu:
   * Menu's name (as string)
   * Menu's version (as integer)
   * Menu's creation date (as string)
3. Annotate the class with @ConfigurationProperties and set a valid prefix of your choice (e.g. `pizza.menu`).
4. Add properties defined by `MenuProperties` to `application.properties`.
5. Inject `MenuProperties` to `PizzaService` and print menu's meta-data before listing all the pizzas.

## Using conditional beans
1. In `application.properties` define a new property `pizza.repository.type=json`.
2. Go to `PizzaRepository` and modify the in-memory and JSON repositories by annotating them with
@ConditionalOnProperty.
   1. `InMemoryPizzaRepository` should be used when `pizza.repository.type=in-memory`
   2. `JsonPizzaRepository` should be used when `pizza.repository.type=json`
3. Toggle the property between `in-memory` and `json` and observer how the output on application's startup changes.

## Using profiles
1. Edit the run configuration in IntelliJ (top right corner, left from the play button) and add active profile 'dev'.
2. In the main resources directory where application.properties remains, create a new file `application-dev.properties`.
3. Overwrite the value of `pizza.repository.type` in `application-dev.properties` to a value different from
the one from `application.properties`.
4. Start the application and observe which repository is being used.
