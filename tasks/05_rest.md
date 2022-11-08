# REST endpoints tasks

1. Create a new endpoint `GET /pizza`, that will call `PizzaService` and return a JSON with list of all pizzas
(adjust the service and repository as needed).
2. Create a new endpoint `GET /pizza/image?name={pizzaName}`, that will get an image for the specified pizza.
The logic for fetching the image should be implemented in `PizzaService`. If a  non-existing pizza name is given,
the service should throw `IllegalPizzaNameException` and the endpoint should respond with 404. Use the same image
`pizza.jpeg` from the main resource directory for every pizza. You can load it with
`ClassPathResource("pizza.jpeg").inputStream`.
3. Create a new endpoint `POST /pizza`, that will accept a `Pizza` object which will be added to the repository.
Use the in-memory `PizzaRepository` and extend `PizzaRepository` and `PizzaService` by a `create` method.
