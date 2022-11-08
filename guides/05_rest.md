# REST endpoints

## Application setup
With Spring Boot you only need to add a dependency to `org.springframework.boot:spring-boot-starter-web`
in order to be able to create REST endpoints. Spring Boot will configure an embedded Tomcat server
and set up all necessary Spring beans.

You are free adjust the Tomcat configuration via [properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.server).

Configuration of the web components can be done by creating a `@Configuration` class, that extends
`WebMvcConfigurer`.

### Debugging hints
All HTTP requests handled by Spring go through the `DispatcherServlet` - this is the central class, that
is responsible for routing requests to specific endpoint methods. It's a great place to start you
debugging sessions.

Spring translates HTTP requests into actual objects (like `String` or `ByteArray`) by using a chain of
`HttpMessageConverter`. If you experience any problems with converting HTTP requests/responses to/from
Kotlin objects, it's a good place to start investigation in the corresponding converter.

## Creating endpoints
REST endpoints are implemented as methods in so-called "REST controller" classes. REST controllers can
be defined with the `@RestController` annotation. Optionally, you can annotate the class also with
`@RequestMapping` containing a common path for all REST endpoints.

To define a REST endpoint, create a method marked with one of the following annotations:
* `GetMapping`
* `PutMapping`
* `PostMapping`
* `PatchMapping`
* `DeleteMapping`

As annotation's parameter you may specify the path of the endpoint.
```kotlin
@RestController
@RequestMapping("/users") // /users will be prepended to every endpoint
class UserEndpoint(private val userService: UserService) {
    
    @GetMapping
    fun getAll(): List<User> = userService.getAllUsers()
    
    @GetMapping("/active")
    fun getActive() = userService.getActiveUsers()
}
```

## Endpoint parameters
Various HTTP components can be made accessible as controller method's parameters. Parameters can be simply bound by
using an appropriate annotation. The most commonly used components are:
* Path parameters (bound via `@PathVariable`)
* Query parameters (bound via `@ReuqestParam`)
* HTTP-Headers (bound via `@RequestHeader`)
* Cookies (bound via `@CookieValue`)

```kotlin
@RestController
@RequestMapping("/users")
class UserEndpoint(private val userService: UserService) {
    
    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long) = userService.getById(id)
    
    @GetMapping("/search")
    fun search(@RequestParam(name = "search", required = true, defaultValue = "") searchTerm: String) =
        userService.search(searchTerm)

    @GetMapping("/stats")
    fun stats(@CookieValue(name = "lang", required = false, defaultValue = "en") preferredLanguage: String) =
        userService.getStats(preferredLanguage)
}
```

## Producing responses
Endpoint methods may return any type and the returned type determines the default Content-Type of the HTTP response.
The most commonly returned types are:
* `String` (assumes content-type `text/html`)
* `ByteArray` (assumes content-type `text/html`)
* Plain old Java objects (assumes content-type `application/json`)
* `ResponseEntity<T>` (assumes content-type from the underlying generic type)

`ResponseEntity<T>` may be used to generate a more detailed response with specific headers and status code.

Additionally, in the `@*Mapping` annotations you can explicitly specify the response type by using the `produces`
parameter. This is particularly useful when returning "generic" data types like `ByteArray`. 

```kotlin
@RestEndpoint
@RequestMapping("/users")
class UserEndpoint {

    // Will return user object as JSON
    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long): User? =
        userService.getById(id)

    @GetMapping("/{id}/avatar", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getAvatar(@PathVariable("id") id: Long): ByteArray =
        userService.getAvatarById(id)
    
    // Will return string response with status OK and a custom header
    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable("id") id: Long): ResponseEntity<String> {
        userService.getById(id)
        return ResponseEntity
            .status(HttpStatus.OK)
            .header("X-My-Custom-Header", "value")
            .body("User deleted")
    }
}
```

## Consuming requests
Body of an HTTP request may be easily bound to the REST endpoint method's parameters. There are few common
ways to do that:
* Using @RequestBody to bind only the request's body
* Using RequestEntity<T> to bind the request entity
* Using HttpServletRequest to bind "raw" servlet request

```kotlin
@RestEndpoint
@RequestMapping("/users")
class UserEndpoint {

    // Binding request body directly with @RequestBody
    @PostMapping
    fun createUser(@RequestBody user: User) {
        userService.create(user)
    }

    // Binding request entity, other info like headers or request path are available
    @PostMapping
    fun updateUser(request: RequestEntity<User>) {
        userService.create(request.body)
    }

    // Binding low-level servlet request
    @PostMapping
    fun updateUser(request: HttpServletRequest) {
        val user = ObjectMapper().readValue<User>(request.inputStream)
        userService.create(user)
    }
}
```

You can also manually restrict content-type, that the endpoint can consume by using the `consumes` parameter.
```kotlin
@PostMapping("/avatar", consumes = [MediaType.IMAGE_JPEG_VALUE])
fun uploadAvatar(image: ByteArray) = TODO()
```

## Handling exceptions
[Spring documentation](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc)


Exceptions thrown in REST endpoint methods are usually translated to a corresponding HTTP error response.
Spring takes care of basic scenarios like:
* Invalid request rejected with a 400 error
* Denied access rejected with a 403 error
* Unexpected error producing an 500 error

By default, all unhandled business exceptions will produce a 500 status.

You can implement your own exception handling in few ways.

### Handling exceptions locally
You can handle all exceptions that are thrown in a specific endpoint class by adding methods annotated
with `@ExceptionHandler`. The scope of exception handling will be limited only to the class, where
exception handlers are declared.

You can customize the HTTP status with @ResponseStatus and also provide an error object by returning
it from the method. By default, the error object will be attached as response body.

```kotlin
@RestController
@RequestMapping("/users")
class UserEndpoint(private val userService: UserService) {
    
    @GetMapping
    fun getAll(): List<User> = userService.getAll()

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoUsersFoundException::class)
    fun noUsersFound(): CustomError = 
        CustomError(code = 123, message = "No users found")
}
```

### Handling exceptions globally
You can use `@ExceptionHandler` also as a global construct to handle exceptions for all REST endpoints
centrally. For this, you need to create an error handler class annotated with `@RestControllerAdvice`
and place your handler methods there.

```kotlin
@RestControllerAdvice
class ErrorHandler {

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException::class)
    fun runtimeException(): CustomError = 
        CustomError(code = 1, message = "Unexpected error")
}
```