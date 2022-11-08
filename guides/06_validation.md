# Validation

## Validation with JSR-303 
By including `org.springframework.boot:spring-boot-starter-validation` as dependency, you're enabled to use
bean validation framework (JSR-303). The framework allows you to validate bean fields by simply annotating them
with constraints.

The list of all built-in constratints can be found [here](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#section-builtin-constraints).
```kotlin
class User(
    @field:NotBlank
    @field:Size(max = 32)
    val firstName: String,

    @field:NotBlank
    @field:Size(max = 64)
    val lastName: String,

    @field:Email
    val email: String,

    @field:Positive
    val age: Int
)
```

You can also create [custom constraints](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-customconstraints),
however this should be done only for universal constraints, that can be imposed directly on the field. Prefer
to implement complex business constraints in a dedicated validator class as creating custom constraints is pretty
verbose.

### Declarative validation
You can tell Spring to run validation for a method parameter by annotating the parameter with `@Validated`. On
validation errors, Spring will throw `MethodArgumentNotValidException`. You may want to process the exception
to a user-friendly error response via `@ExceptionHandler`.

```kotlin
@PostMapping
fun create(@Validated @RequestBody user: User) {
    userService.create(user)
}
```

### Programmatic validation
As an alternative to declarative validation, you can write your own validator class and check all the JSR-303
constraints from there. After that you could perform your own business validations.

```kotlin
@Component
class UserValidator(private val validator: Validator) {

    fun validate(user: User) {
        val violations = validator.validate(user)
        if (violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }
        
        // Do some custom validations otherwise...
    }
}
```

## Kotlin-native validation libraries
Meanwhile, there are few Kotlin-native validation libraries, that leverage Kotlin-DSL style:
* [Konform](https://www.konform.io/)
* [Valiktor](https://github.com/valiktor/valiktor)

Consider using them as more lightweight and idiomatic alternative to JSR-303.