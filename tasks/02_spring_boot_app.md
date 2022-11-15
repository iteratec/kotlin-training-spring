# Spring Boot application tasks
1. Implement the interface `CommandLineRunner` in your Spring Boot application class `PlaygroundApplication`, so that 
"Hello Spring!" is printed at application's startup.
2. Add the entry `logging.level.org.springframework.boot=debug` to `application.properties` and start the application.
Check out how Spring Boot configuration is logged. Then remove the entry.
3. Exclude `DataSourceAutoConfiguration` from your Spring Boot application by using `exclude` parameter of
the `@SpringBootApplication` annotation. This will disable autoconfiguration of data sources & JPA. Start the
application and check how the output changes. Then remove the exclusion.
4. Add the entry `spring.banner.location=classpath:custom-banner.txt` to `application.properties`, run the application
and check how the banner displayed at application's start changes.
5. Add the entry `spring.h2.console.enabled=true` to `application.properties`. Then navigate to
[http://localhost:8080/h2-console](http://localhost:8080/h2-console). You just activated a web-console for our embedded
database! 
