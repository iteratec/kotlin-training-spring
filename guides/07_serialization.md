# Kotlinx serialization
[kotlix serialization docs](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/basic-serialization.md)
## Enabling kotlinx serialization
Spring offers native support for kotlinx serialization. If your project includes
kotlinx serialization dependencies, kotlix serializer will take precedence over
Jackson serializer.

To enable kotlinx serialization you have to:
* Include the dependency to `org.jetbrains.kotlinx:kotlinx-serialization-json`
* Add kotlix serialization plugin to your project
* Annotate serializable classes with `@Serializable`
```kotlin
// build.gradle.kts
plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}
```

## Renaming serialized properties
Properties in serialized representation may have different names as the properties in the data class. Use 
`@SerialName` to use a custom name for a property.
```kotlin
// "username" will be serialized as "login":
// { "login": "hans", "password": "abc" }
@Serializable
data class User(
    @SerialName("login")
    val username: String,
    val password: String
)
```

## Serializing default properties
Default properties won't be serialized by default, but you can change this behavior by annotating a property
with @EncodeDefault.

```kotlin
// "enabled" will not be serialized by default:
// { "login": "hans", "password": "abc" }
@Serializable
data class User(
    @SerialName("login")
    val username: String,
    val password: String,
    @EncodeDefault val enabled: Boolean = true
)
```

## Ignoring properties
You can mark a property as `@Transient` to exclude it from serialization.

```kotlin
// "enabled" will be excluded from the serialization
// { "login": "hans", "password": "abc" }
@Serializable
data class User(
    @SerialName("login")
    val username: String,
    val password: String,
    @Transient val enabled: Boolean
)
```

## Serializing dates
[kotlix-datetime documentation](https://github.com/Kotlin/kotlinx-datetime)

There's an extra multiplatform library `org.jetbrains.kotlinx:kotlinx-datetime:0.4.0`, that allows you to work with
dates on any platform in a uniform way. The library is conceptually similar to the `java.time` API.
```kotlin
@Serializable
data class User(
    @SerialName("login")
    val username: String,
    val password: String,
    val enabled: Boolean = true
)
```

## Global configuration
You can adjust the global serializer configuration by creating a `KotlinSerializationJsonHttpMessageConverter` bean.
Spring Boot will automatically add this bean to the message converter list used by Spring.

```kotlin
@Bean
fun kotlinConverter() = KotlinSerializationJsonHttpMessageConverter(Json {
    encodeDefaults = false
    explicitNulls = true
    prettyPrint = true
})
```
