## Gradle tasks
1. Run the Gradle goal ``verification/test`` and analyze the reported problem.
2. Add the missing Kotlin compiler plugins ``plugin.spring`` and ``plugin.jpa`` to ``build.gradle.kts``.
3. Add missing dependency ``org.springframework.boot:spring-boot-starter-validation``.
4. Add a new plugin ``org.jetbrains.kotlinx.kover`` (version 0.6.1) and check
how it affects the goals in the ``verification`` group. Execute the ``koverHtmlReport`` and
checkout the results in ``build/reports/kover``.