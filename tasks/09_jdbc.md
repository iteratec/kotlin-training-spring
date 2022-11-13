## JDBC support tasks
1. Initialize the H2 database with a schema for the pizza table and insert few sample rows. Refer to the scripts
`schema.sql` and `data.sql` in the solution projects.
2. Create a new repository implementation `JdbcPizzaRepository` and make it active by setting the property 
`pizza.repository.type=jdbc`. Implement all required method by using `JdbcTemplate`.
3. Add a new endpoint `/pizza/count` that will output the total number of pizza in the repository. Extend 
`PizzaEndpoint`, `PizzaService` and `PizzaRepository` accordingly.
