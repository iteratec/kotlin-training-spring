# Spring test tasks

1. Create a full integration test for `PizzaService`:
   1. Create a new test class `PizzaServiceTest` and annotate it with `@SpringBootTest`.
   2. Inject the `PizzaService` instance by constructor injection of by `@Autowired lateinit var pizzaService: PizzaService`.
   3. Create a new method annotated with `@Test` and test if `pizzaService.getAll()` returns a valid result. It should
      contain at least one specific pizza. You can use `assertThat(pizzaList).contains(Pizza(name = expectedName, price = expectedPrice))` to check if the list contains a specific pizza.
