# JDBC support

## Defining data sources
Spring Boot will automatically set up an in-memory data source, when a dependency to an embeddable database like H2 or 
HSQLDB is present. You can alter the configuration by editing the properties prefixed with `spring.datasource`.

When using an external database, you can similarly specify the configuration in the `spring.datasource` properties.
Spring Boot will create a single pooled datasource using the Hikari connection pool.

```
spring.datasource.url=jdbc:mysql://localhost/test
spring.datasource.username=dbuser
spring.datasource.password=dbpass
```

You can use the created data source by just injecting the `DataSource` bean:
```kotlin
@Repository
class MyRepository(dataSource: DataSource) {
    private val jdbcTemplate: JdbcTemplate = JdbcTemplate(dataSource)
}
```

## Custom configuration & multiple data sources
To have the full flexibility when creating a data source, you can use the data source builder. This also enables you
to easily create multiple data sources.

```kotlin
@Configuration
class DataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasource.master")
    fun masterDataSource(): DataSource = DataSourceBuilder.create().build()

    @Bean
    @ConfigurationProperties("app.datasource.fallback")
    fun fallbackDataSource(): DataSource = DataSourceBuilder.create().build()
}
```

## Initializing the database
Normally, you will initialize your databases via a specialized library like Flyway or Liquibase. For simple cases,
you can use the simple initialization mechanism provided by Spring Boot. Spring Boot will read on startup the files
`schema.sql` and `data.sql` and execute the against the current database.

## Using JdbcTemplate
`JdbcTemplate` provides you methods to perform basic SQL operations. Since `JdbcTemplate` is thread-safe, you can
instantiate it as a bean and reuse it in the whole application.

### Querying primitive objects
Spring can automatically convert primitive types resulting from SQL queries. In these cases you don't have to write
a custom mapper, that will transform the result set into domain objects.

```kotlin
val userId: Long? = jdbcTemplate.queryForObject("select id from user where name = 'Alice' ", Long::class.java)
```

### Querying complex objects
For complex domain classes you need to provide a way to map the SQL results to your domain objects. Spring offers the
abstraction `RowMapper` to perform this task. Row mapper takes the SQL result set and returns an instance of a domain
object.

```kotlin
fun queryUser(id: Long): User?  = try {
    jdbcTemplate.queryForObject("select * from user where id = ?", UserRowMapper(), id)
} catch (ex: EmptyResultDataAccessException) {
    null
}

class UserRowMapper : RowMapper<User> {
    override fun mapRow(rs: ResultSet, rowNum: Int): User = User(
        id = rs.getInt("id"),
        name = rs.getString("name"),
        enabled = rs.getBoolean("enabled")
    )
}
```
### Querying lists
Querying lists is very similar to querying single objects. Just user the `query()` methods instead of `queryForObject()`.
```kotlin
fun queryAllUsers(): List<User> = jdbcTemplate.queryForObject("select * from user", UserRowMapper())
```

### Inserting new records
To insert a new record to the database you may use the `update` method of `JdbcTemplate`.

```kotlin
fun addUser(user: User) {
    jdbcTemplate.update("insert into user (name, enabled) values (?, ?)", user.name, user.enabled)
}
```

Alternatively, you can use the simpler API offered by the `SimpleJdbcInsert` class.

```kotlin
override fun create(user: User) {
        SimpleJdbcInsert(jdbcTemplate)
            .apply { withTableName("user") }
            .run { execute(user.toColumnMap()) }
}

private fun User.toColumnMap() = mapOf("name" to name, "enabled" to enabled)
```

### Deleting records
To delete a record, you may also use the `update` method.
```kotlin
fun delete(id: Int) {
    jdbcTemplate.update("delete from user where id = ?", id)
}
```
