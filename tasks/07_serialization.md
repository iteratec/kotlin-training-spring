# Kotlinx serialization tasks
1. Add the dependency `org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1` and the plugin
`plugin.serialization` to the project. Then, add a new boolean field `vegan` to the `Pizza`
class with a default value `false` and mark the `Pizza` class as `@Serializable`. Run the
application and check the output of the `/pizza` endpoint.
2. Annotate the `vegan` field with `@EncodeDefault` and `@SerialName("veganFriendly")`. Run the
   application and check the output of the `/pizza` endpoint.
3. Add a new dependency `org.jetbrains.kotlinx:kotlinx-datetime:0.4.0` to the project. Then, add
a new field `createdOn` of type `Instant` to the `Pizza`. Provide a constant default value to
the field and mark it with `@EncodeDefault`. Run the application and check the output of the 
`/pizza` endpoint.
4. Remove all `@EncodeDefault` annotation from the `Pizza` class and instead enable encoding
defaults globally. 
