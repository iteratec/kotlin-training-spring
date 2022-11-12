# Spring security

Spring security allows you to constraint access to your REST endpoints and to manage various authentication
mechanisms.

The basic concepts of Spring Security are represented by the following classes:
* `FilterChainProxy` - intercepts every request and puts in through a matching `SecurityFilterChain`
* `Authentication` - data object containing incoming credentials
* `AuthenticationManager` - takes an Authentication object and verifies it by delegating to registered AuthenticationProviders
* `AuthenticationProvider` - checks provided credentials, supports only one authentication method
* `UserDetailsService` - interface used to abstract a user repository
* `SecurityContextHolder` - static class containing the currently authenticated user
* `SecurityFilterChain` - chain of filters that may perform authentication of restrict access to an endpoint

Spring Security is mainly configured by creating a custom instance of a `SecurityFilterChain`. This is done in
a regular `@Configuration` class. Spring provides you a `HttpSecurity` instance, which is practically a builder
of `SecurityFilterChain`.

Starting from Spring Security 5.4, the security configuration can be expressed as Kotlin-DSL. Make sure
to have the `invoke` import (autocompletion is a bit clunky here).

## Protecting endpoints
Endpoints can be secured in the `authorizeRequests` section. Authorizations are matched in the 
definition order. Every authorization consists of a request matcher (or convenience alternative
like request path) and assertion on user roles or authentication state. See [Expression-Based Access Control](https://docs.spring.io/spring-security/reference/servlet/authorization/expression-based.html)
for all possible assertions.

```kotlin
// Import needed to use Kotlin-DSL
import org.springframework.security.config.web.servlet.invoke

@Configuration
class WebSecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            // This SecurityFilterChain will match only requests starting with /api
            // In complex apps you can have other chains handling other contexts.
            securityMatcher("/api/**", "/login")
            
            // Access restrictions
            authorizeRequests {
                authorize("/api/user/**", authenticated)
                authorize(HttpMethod.GET, "/api/settings", authenticated)
                authorize(HttpMethod.POST, "/api/settings", hasRole("admin"))
                authorize(CustomRequestMatcher(), hasRole("user"))
                authorize(anyRequest, permitAll)
            }

            formLogin { }
            csrf { disable() }
        }

        return http.build()
    }
}
```

## Defining authentication manager
Authentication manager is responsible for the actual authentication of incoming `Authentication`
objects which contain user credentials (user & password, access token). Usually, authentication manager
holds a list of authentication providers and delegates the authentication to them. Each provider
handles one specific authentication scheme. For instance, you could have one authentication manager with
an LdapAuthProvider and DatabaseAuthProvider. This way you could authenticate the user against an LDAP
server or database (whatever matches first).

`AuthenticationManager` has one relevant implementation - the `ProviderManager`.

Configuration of `AuthenticationProviders` varies strongly, depending on the authentication mechanism.

One of the commonly used authentication providers is `DaoAuthenticationProvider` which uses an abstraction
of `UserDetailsService` to fetch relevant user data like username or password hash. By using an
appropriate implementation of `UserDetailsService` you can easily authenticate against a database,
in-memory repository or a web service. Spring provides two relevant `UserDetailsService` implementations:
* `JdbcUserDetailsManager` - allows to access users stored in a database table
* `CachingUserDetailsService` - provides caching and delegates to another service
 
```kotlin
@Bean
fun securityFilterChain(http: HttpSecurity, authenticationProvider: AuthenticationProvider): SecurityFilterChain {
    http {
        // Create auth manager with one auth provider
        authenticationManager = ProviderManager(authenticationProvider)
    }

    return http.build()
}

// Create an AuthenticationProvider authenticating against an in-memory user repository.
@Bean
fun daoAuthenticationProvider(): AuthenticationProvider {
    val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
    val userDetailsService = InMemoryUserDetailsManager().apply {
        createUser(User.withUsername("alice").passwordEncoder(passwordEncoder::encode).password("password").roles("admin").build())
        createUser(User.withUsername("bob").passwordEncoder(passwordEncoder::encode).password("password").roles("user").build())
    }

    return DaoAuthenticationProvider().apply {
        setPasswordEncoder(passwordEncoder)
        setUserDetailsService(userDetailsService)
    }
}
```

## Defining authentication entry points
Authentication entry point is a response delivered when user authentication is required, but no
authentication data has been received. For instance, when HTTP basic authentication is configured,
but no "Authorization" header has been passed. You can customize this type of response by implementing
the AuthenticationEntryPoint interface and setting it in the `exceptionHandling` section.

```kotlin
@Bean
fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http {
        exceptionHandling {
            authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.NOT_FOUND)
        }
    }

    return http.build()
}
```

## Adding custom filters
You can modify the `SecurityFilterChain` by adding custom filters. This can be useful when implementing custom
authentication methods.

```kotlin
@Bean
fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http {
        // Adds MyCustomFilter before UsernamePasswordAuthenticationFilter
        addFilterBefore<UsernamePasswordAuthenticationFilter>(MyCustomFilter())
        
        // Adds MyCustomFilter after UsernamePasswordAuthenticationFilter
        addFilterAfter<UsernamePasswordAuthenticationFilter>(MyOtherCustomFilter())
    }

    return http.build()
}
```

## Accessing security context
The current security context is accessible via the SecurityContextHolder object. The context contains
the username, roles and other information about the currently logged-in user. You can access the context
as follows:

```kotlin
fun printUser() {
    val username = SecurityContextHolder.getContext().authentication?.principal
    println("Current user = $username")
}
```

Alternatively, you can also inject the security context as method parameter using `@CurrentSecurityContext`.
```kotlin
fun printUser(@CurrentSecurityContext context: SecurityContext) {
    val username = context.authentication?.principal
    println("Current user = $username")
}
```

## Custom security-checks with @PreAuthorize
You can include custom authorization checks by annotating a method with `@PreAuthorize`. The method
takes a Spring expression as parameter. Most notably it can call any bean method, that performs a
check.

```kotlin
@RestController
class UserResource {
    
    @PreAuthorize("@userPermissions.canRead(#id)")
    @GetMapping("/user/{id}")
    fun getById(@PathVariable("id") id: Long): User? = TODO()
}

@Component("userPermissions")
class UserPermissions {
    canRead(id: Long): Boolean = true
}
```


