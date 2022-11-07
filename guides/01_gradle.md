# Gradle basics
* [Gradle Kotlin plugin docs](https://kotlinlang.org/docs/gradle.html)
* [Gradle Kotlin multiplatform plugin docs](https://kotlinlang.org/docs/multiplatform-dsl-reference.html)
* [Gradle docs](https://docs.gradle.org/current/userguide/getting_started.html)

## Repository management
[Gradle documentation](https://docs.gradle.org/current/userguide/declaring_repositories.html)

* Use ``repositories`` block in ``build.gradle.kts`` to specify project's dependency repositories:
```kotlin
repositories { 
    mavenCentral()
    maven {
        url = uri("https://repository.jboss.org/maven2")
    }
}
``` 
* To share repository configuration between multiple projects,
place the following snippet in ``settings.gradle.kts``:
```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
```
* Repositories for Gradle plugins are managed differently. Use pluginManagement block in ``settings.gradle.kts`` to adjust them:
```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
```


## Dependency management
[Gradle documentation](https://docs.gradle.org/current/userguide/declaring_dependencies.html)

* You can manage dependencies in the ``dependencies`` block in ``build.gradle.kts``:
```kotlin
dependencies {
    // Compile-time dependency to another local project
    implementation(project(":utils"))
    
    // Compile-time dependency to external library with one exclusion of a transitive dependency
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "commons-collections", module = "commons-collections")
    }
    
    // Compile-time test dependency to external library
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    
    // Runtime dependency, not needed for compilation, but needed in runtime 
    runtimeOnly("com.h2database:h2")
}
```
* You can share a specific version of an artifact family by declaring it as property in ``gradle.properties``:
```kotlin
// gradle.properties
versionKotest=5.0.2

// build.gradle.kts
dependencies {
    val versionKotest: String by project
    implementation("io.kotest:kotest-assertions-core:$versionKotest")
    implementation("io.kotest:kotest-framework-engine:$versionKotest")
}
```
* Alternatively, you can use new [version catalog](https://docs.gradle.org/current/userguide/platforms.html) feature to centrally manage dependency versions:
```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("groovy", "3.0.5")
            library("groovy-core", "org.codehaus.groovy", "groovy").versionRef("groovy")
            library("groovy-json", "org.codehaus.groovy", "groovy-json").versionRef("groovy")
            library("groovy-nio", "org.codehaus.groovy", "groovy-nio").versionRef("groovy")
            library("commons-lang3", "org.codehaus.groovy:groovy-nio:3.0.5")
        }
    }
}

// build.gradle.kts
dependencies {
    implementation(libs.groovy.core)
    implementation(libs.commons.lang3)
}
```

## Plugin management
* You can centrally manage Gradle plugins by specifying them in the root ``build.gradle.kts``:
````kotlin
// root build.gradle.kts
plugins {
    kotlin("jvm") version "1.6.21" apply false
    kotlin("plugin.spring") version "1.6.21" apply false
    kotlin("plugin.jpa") version "1.6.21" apply false
}

// ...then the plugin can be applied in a subproject without specifying the version
// subproject's build.gradle.kts
plugins {
  kotlin("jvm")
  kotlin("plugin.spring")
  kotlin("plugin.jpa")
}
````
* Alternatively, you can use the new version catalog feature:
````kotlin
// settings.gradle.kts
dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      plugin("jmh", "me.champeau.jmh").version("0.6.5")
    }
  }
}

// build.gradle.kts
plugins {
  alias(libs.plugins.jmh)
}
````

## Compiler plugins & options 
* Spring usually requires two Kotlin plugins to work:
  * spring-plugin - opens all classes by default
  * jpa-plugin - generates a no-arg constructor for entities
````kotlin
plugins {
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}
````
* You can tweak Kotlin compiler's options with the task block in ``build.gradle.kts``:
```kotlin
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
```

## Building projects
* Kotlin & Spring Boot plugins provide several goals, that you can use to build & run the project:
  * ``build/clean`` - deletes all build outputs
  * ``build/assemble`` - compiles the project & builds output artifacts
  * ``build/build`` - same as assemble + executes unit tests
  * ``verification/check`` - runs unit tests and performs other checks if configured (e.g. via checkstyle plugin)
  * ``verification/test`` - runs unit tests
  * ``application/bootRun`` - runs the Spring Boot application (but it's better run directly from IntelliJ)