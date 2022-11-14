# REST endpoints tasks

## Return all pizzas
1. Create a new controller class `PizzaEndpoint` and annotate it accordingly with `@RestController` and `@RequestMapping("/pizza")`.
2. In the controller create a new method `getAll()` annotated with `@GetMapping`. This will create a new HTTP endpoint `GET /pizza`.
3. Create a new method `getAll()` in `PizzaService`, that will return all pizzas
from the repository.
4. Inject `PizzaService` into the controller and use it to return all pizzas in the `GET /pizza` endpoint. Start the application and check the output by opening [http://localhost:8080/pizza](http://localhost:8080/pizza) in the browser.

## Return a pizza image 
1. Implement a new method `getImage(name: String): ByteArray` in  `PizzaService`. The method should fulfill the following specification:
   * If a non-existing pizza name is given, the method should throw an `IllegalPizzaNameException`. Create the exception in the same file as the service. It should inherit from RuntimeException.
   * If a valid pizza name is given, the method should return an image as `ByteArray`. Use `ClassPathResource("pizza.jpeg").inputStream.readAllBytes()` to load the image.
2. Create a new endpoint `GET /pizza/image?name={pizzaName}`, that will get an image for the specified pizza. Use `@GetMapping` with appropriate path and `@RequestParameter` to bind the name parameter. The endpoint will return
   images - use `produces = [MediaType.IMAGE_JPEG_VALUE]` parameter in `@GetMapping` to specify the output content type properly.
3. Start the application and query the created endpoint with a valid and
invalid pizza name.
4. Add a new `@ExceptionHandler` in the controller class that will handle the `IllegalPizzaNameException` exception. It should return the HTTP status 404
 when the exception occurs.

## Creating a new pizza
1. Make your pizza list in the `PizzaRepository` mutable and create a new method
`create(pizza: Pizza)` that will add a new pizza to the list.
2. Create a similar method `create(pizza: Pizza)` in the `PizzaService`. It should delegate the execution to the `PizzaRepository`.
3. Create a new endpoint `POST /pizza`, that will accept a `Pizza` object which will be added to the repository. Use `@RequestBody` to bind the
HTTP request's body to the method parameter.
4. Start the application and check if the endpoint works correctly by listing
all the pizzas, adding a new one, and then listing all pizzas again.
