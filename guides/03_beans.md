# Spring beans
[Spring documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-definition)

Spring beans are object instances, that are managed by the Spring container. Spring is responsible for creating and
destroying the beans, based on the declarative configuration specified by the developer. Thanks to dependency
injection, developer doesn't have to resolve dependencies of every bean manually and can easily replace specific 
beans without doing many changes in the application.

## Bean scopes
Beans may have different scopes which define the lifecycle of the bean:
* `singleton` - one instance of a bean is created at application's startup, and it's reused everywhere
* `prototype` - a new instance of a bean is created at every injection point
* `request` - bean's lifespan is bound to an HTTP request, a new instance of a bean is created of every HTTP request
* `session` - bean's lifespan is bound to an HTTP session, a new instance of a bean is created of every HTTP session

Most of the time you will need only the `singleton` scope which is used to create all services, repositories and web
controllers.

## Declaring beans
Beans may be declared in many ways. The most common approaches are:
* Annotating a class with a stereotype-annotations like `@Component` or `@Service`.
* Declaring a bean with `@Bean` annotation in a `@Configuration` class
* Using [Kotlin-DSL](https://docs.spring.io/spring-framework/docs/current/reference/html/languages.html#kotlin-bean-definition-dsl)

### Declaring beans with annotations
Classes annotated with `@Component`, `@Controller`, `@Repository` or `@Service` will be automatically picked up
by Spring and a singleton instance of this class will be created by default.

Annotation type doesn't have any effect, but you should pick the most appropriate one.
```kotlin
@Component
@Scope("prototype")
class NameGenerator

@Service
class PetService
```

### Declaring beans with @Bean
You can define classes annotated with `@Configuration`. Those classes usually contain methods annotated
with `@Bean`. By default, Spring will pick up every configuration class and call every bean method once.
The result of every method will be added to the Spring context as a bean. You can use this bean creation
method, when:
* you want to instantiate a class provided by other library (e.g. ``DataSource``)
* complex logic has to be executed in order to create a bean
* you want to bundle associated beans

```kotlin
@Configuration
class BeanConfig {
    
    @Bean
    @Scope("prototype")
    fun nameGenerator(): NameGenerator = NameGenerator()

    @Bean
    fun petService(): PetService = PetService()
}
```

### Declaring beans with Kotlin-DSL
Spring provides Kotlin-DSL, that allows you to declare beans in a programmatic way. To make this work
a slight adjustment of the `main` function is required:

```kotlin
val beans = beans {
    bean<NameGenerator>(scope = BeanDefinitionDsl.Scope.PROTOTYPE)
    bean {
        println("Pet service created")
        PetService() 
    }
}

fun main(args: Array<String>) {
    SpringApplicationBuilder(PlaygroundApplication::class.java)
        .initializers(beans)
        .build()
        .run()
}
```

## Dependency injection
Spring manages all dependencies between the beans for you. As a developer you only need to declare
the dependencies of your bean and Spring will take care of wiring up all the bean instances. Dependencies
may be declared in two main ways:
* In the constructor (recommended)
```kotlin
class PetService(private val petRepository: PetRepository)
```
* Via `@Autowired` fields
```kotlin
class PetService {
    
    @Autowired
    private lateinit var petRepository: PetRepository
}
```
### Dependency resolution
By default, beans are injected by type. Any supertype of the required bean may be used to inject it.
For instance, if the required bean implements an interface or extends some base class, you can use
the interface or the base class to inject it:
```kotlin
@Repository
class PetRepositoryImpl : BasePetRepository(), PetRepository

// All variants will work
class PetService(private val repo: PetRepositoryImpl)
class PetService(private val repo: BasePetRepository)
class PetService(private val repo: PetRepository)
```

However, sometimes you may have multiple beans implementing the same interface. In these cases type is not
sufficient to unambiguously identify the required bean. This could happen when you work with two relational
databases and both are reached via a ``DataSource`` instance. Spring offers two mechanisms to mitigate this
issue.

### Primary beans
You can mark a bean as `@Primary`. When multiple beans of a given type are present, the primary bean
will always be selected and this way it's clear which bean should be injected:
```kotlin
@Configuration
class DataSourceConfig {
    
    @Bean
    @Primary
    fun masterDataSource(): DataSource = SampleDataSource("jdbc:mysql://localhost:3306")

    @Bean
    fun fallbackDataSource(): DataSource = SampleDataSource("jdbc:mysql://localhost:4406")
    
    // Will use masterDataSource, because it's marked as primary
    @Bean
    fun jdbcTemplate(dataSource: DatSource): JdbcTemplate = JdbcTemplate(dataSource)
}
```

### Named beans
The other method of avoiding ambiguities in dependency injection process is to use named beans. Any
bean can have a unique string identifier, that can be specified, when injecting a dependency:
```kotlin
@Configuration
class DataSourceConfig {
    
    @Bean("masterDataSource")
    // ...or extra @Qualifier("masterDataSource") annotation
    fun masterDataSource(): DataSource = SampleDataSource("jdbc:mysql://localhost:3306")

    @Bean("fallbackDataSource")
    fun fallbackDataSource(): DataSource = SampleDataSource("jdbc:mysql://localhost:4406")
    
    // Will use masterDataSource, because it's marked as primary
    @Bean
    fun jdbcTemplate(@Qualifier("masterDataSource") dataSource: DatSource): JdbcTemplate =
        JdbcTemplate(dataSource)
}
```

## Retrieving beans programmatically
In rare cases you may need to programmatically fetch an instance of a bean. To do that you can use
the ``ApplicationContext`` interface:
````kotlin
class PetPrinter(private val context: ApplicationContext) {
    
    fun listPets() {
        val petRepository = context.getBean(PetRepository::class.java)
        println(petRepository.getAll().joinToString())
    }
}
````

## Life-cycle callbacks
When creating and destroying beans, Spring will call specific life-cycle methods on the bean, if they're
present. You may use these methods to finalize the bean initialization or to clean up the resources before
the bean is destroyed:
````kotlin
class TidyBean {
    
    @PostConstruct
    fun init() {
        // finalize initialization
    }
    
    @PreDestroy
    fun cleanup() {
        // close all resources
    }
}
````
You can also implement the methods `afterPropertiesSet()` and `destroy()` instead of using annotated methods.
These special method names will be also detected by Spring and methods will be called at corresponding life-cycle
stage.