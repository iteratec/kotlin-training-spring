# Spring security tasks
1. Secure the `/pizza/image` endpoint, so that only authenticated users may call it. All other endpoints should be
accessible for anyone. Do it by editing the already existing configuration in `WebSecurityConfig`. Try to call the
endpoint as unauthenticated user and check the response.
2. Change the response status for unauthenticated users to 402 Payment Required. This can be archived by setting a custom `authenticationEntryPoint`. You can use the class 
HttpStatusEntryPoint provided by Spring to produce the response.
3. Remove the custom authentication entry point configured in the last point and add HTTP basic authentication instead.
Spring provides the `httpBasic` method, that allows you to add and configure HTTP basic authentication. Configure an
instance of `InMemoryUserDetailsManager` as a bean and define a single user. Spring will automatically pick it up
and use it as a source of user data.
4. Create a new endpoint class `UserEndpoint` in a new package `user` and define a single endpoint
`GET /users/current`. The endpoint should return the name of the currently logged-in user as a string. Make the
endpoint accessible only for authenticated users.
