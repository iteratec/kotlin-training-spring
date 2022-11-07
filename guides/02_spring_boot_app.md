## Spring Boot application
1. Spring Boot application is created by annotating the application's class with 
``@SpringBootApplication`` and creating a corresponding main() method, which will start the application:
    ````kotlin
    @SpringBootApplication
    class SolutionApplication
    
    fun main(args: Array<String>) {
        runApplication<SolutionApplication>(*args)
    }
    ````
2. When the method ``runApplication`` executes, Spring will pick up all annotated components present
in the subpackages and add them to the application's context.
3. Spring Boot application should **always** be placed in the top-level package, so that no other Spring
component is in a package above the Spring Boot application - this is required by component scanning
mechanism of Spring.
4. Spring application class may implement the interface ``CommandLineRunner`` to perform extra logic on
application's start.
5. Try to keep the Spring boot application class as simple as possible and avoid placing too many
annotations on it. Instead, prefer to create separate configuration classes for every function. This
will make it easier to create tests, that spawn only a part of the application context (i.e. only
the web-layer, but no persistence).

## Spring Boot autoconfiguration
The main value of Spring Boot is the ability to automatically configure Spring context depending on the
set properties and dependencies added to the project. For instance, to start an embedded Tomcat server
you only need to specify ``org.springframework.boot:spring-boot-starter-web`` as dependency and Spring
Boot will take care of creating and starting a default Tomcat server.

Most of the Spring Boot features can be configured by setting [according properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
in the application.properties/yaml file.

However, in complex projects with a lot of custom configuration, Spring Boot's autoconfiguration
may turn out to be an obstacle. In such cases it's good to be able to find out what Spring
Boot is setting up for us. All auto-configurations can be found in the dependency 
``org.springframework.boot:spring-boot-autoconfigure``. By analysing the source code of the
autoconfiguration classes, you can pretty easily find out which beans are configured and how
the configuration is triggered.
You can also list all autoconfiguration classes and their activation status by setting Spring
Boot logging to debug with ``logging.level.org.springframework.boot=debug`` in `application.properties`.