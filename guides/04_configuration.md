# Application configuration
## Properties
A standalone Spring Boot application is usually configured using static property files.
The default property file is `application.properties` or `application.yml` placed in the root resource
directory.

Spring boot offers huge amount of [predefined properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html),
that allow you to configure various components in the application.

### Injecting properties
You are free to inject any property value to your beans or configuration classes. Spring provides two main
mechanisms to archive this.

#### Injecting with @Value
You can inject any property in the same way as a bean dependency by specifying it in the constructor. 
Property injections have to be additionally annotated with `@Value("\${property-name")}` to indicate which property
should be injected:
```kotlin
class PizzaService(@Value("\${pizza.menu.name}") private val menuName: String)
```
You can also provide a default value, that will be used when the property is not present:
```kotlin
class PizzaService(@Value("\${pizza.menu.name:defaultValue}") private val menuName: String)
```

#### Injecting via @ConfigurationProperties
[Spring documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties.enabling-annotated-types)

When multiple properties associated with the same component are needed, it's often better
to group them into a configuration class. This way, you can reduce the boilerplate code that
emerges when using `@Value` annotations. Spring provides the `@ConfigurationProperties` annotation
which allows to map properties onto fields of a configuration class. This way you can just pass
one configuration object to your bean, instead of injecting many `@Value` values.

To enable configuration properties, you need to first annotate you application with `@ConfigurationPropertiesScan`.
Then you can create configuration classes and inject them into beans:

```kotlin
// application.properties may contain:
// ftp.host=ftp.kotlin.org
// ftp.port=49
// ftp.user=kotlin
// ftp.password=kotlin123

@ConstructorBinding
@ConfigurationProperties(prefix = "ftp")
data class FtpProperties(
    val host: String,
    val port: Int = 21, // Default value used when no property is defined
    val user: String,
    val password: String
)

// You can inject properties like any other bean
class UploaderService(private val ftpProps: FtpProperties)
```

Configuration properies support many different data types like dates, durations and lists.

### Conditional beans
Spring provides the annotation `@ConditionalOnProperty` which allows you to create beans
only if a property is set to a specific value. This can be used to switch between two
implementations of an interface, depending on the property value.

Imagine you have a `RemoteFileCopyService`, that copies files to a remote server. There are
two implementations of this service: `FtpFileCopyService` and `ScpFileCopyService`. You could
switch between those two implementations by providing a property value `file.copy.type=ftp|scp`:

```kotlin
@Configuration
class RemoteFileCopyConfig {

    @Bean
    @ConditionalOnProperty(name = ["file.copy.type"], havingValue = "ftp")
    fun ftpFileCopyService(): RemoteFileCopyService = FtpFileCopyService()

    @Bean
    @ConditionalOnProperty(name = ["file.copy.type"], havingValue = "scp")
    fun scpFileCopyService(): RemoteFileCopyService = ScpFileCopyService()
}
```

Spring Boot provides many other similar annotations like `@ConditionalOnClass`, `@ConditionalOnExpression`, etc.

## Profiles
[Spring documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.profiles)

Profiles are the main mechanism used to control configuration between various deployment
environments. Often an application will define few profiles like `dev`, `test` or `prod` where
every profile contains environment-specific configuration (like database address). You can think of
a profile as a named subset of application's configuration.

### Activating profiles
To activate selected profiles, you can do the following:
* Run the Java process with the `spring.profiles.active` property:

  ``java -Dspring.profiles.active=profile1,profile2 -jar app.jar``

* Pass `spring.profiles.active` parameter to the application:
 
  ```java -jar app.jar --spring.profiles.active=profile1,profile2```

* For development purposes just edit the run configuration in IntelliJ via UI.

### Defining profile-specific properties
A property file may be associated with a profile. Such file has to be named 
`application-{profile}.properties` and it will be loaded once the profile has been activated.

General properties from `application.properties` will still be loaded, but profile-specific
file has higher priority may override some properties. It's a common practice to define
common shared properties in `application.properties` and tweak this base configuration via
profile-specific files.

```
// application.properties
database.type=mysql

// application-dev.properties
database.address=jdbc:mysql:localhost:3306
```

### Defining profile-specific beans
You can activate some beans only when selected profile has been activated. This allows you to
select a specific implementation based on profile or to enable some functions only for a
give profile. Annotate the bean with `@Profile(profileName)` to enable it only for selected
profiles.

```kotlin
// Enables reindex job only for profiles 'staging' and 'prod'
@Component
@Profile("staging,prod")
class ReindexCronJob {
  
  @Scheduled(fixedDelay = 60000)
  fun reindex() { TODO() }
}

@Configuration
class RemoteFileCopyConfig {

  // Uses FTP implementation for profile dev
  @Bean
  @Profile("dev")
  fun ftpFileCopyService(): RemoteFileCopyService = FtpFileCopyService()

  // Uses SCP implementation for every other profile than dev
  @Bean
  @Profile("!dev")
  fun scpFileCopyService(): RemoteFileCopyService = ScpFileCopyService()
} 
```

### Querying active profiles
You can query active profiles programmatically by injecting the `Environment` interface:

```kotlin
@Component
class ProfileLogger(private val env: Environment) {

    fun logProfiles() {
        println(env.activeProfiles)
    }
}
```