# Validation tasks
1. In the last chapter we created an endpoint to save a new `Pizza` into repository. Now, you add few
validations to the `Pizza` object received via the REST endpoint:
   1. Pizza's name should be not blank and have a length between 3 and 16 characters. You can use `@NotBlank` and `@Size` annotations for this.
   2. Pizza's price should be a positive integer. You can use the `@Positive` annotation for this.
   3. Make sure, that validation is triggered. You can use the `@Validated` annotation for this.
2. Check what happens, when you pass an invalid `Pizza` object.  
