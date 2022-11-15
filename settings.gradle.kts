rootProject.name = "spring-training"
include("playground", "solution")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            library("groovy-core", "org.codehaus.groovy:groovy:3.0.5")
            library("groovy-json", "org.codehaus.groovy:groovy-json:3.0.5")
        }
    }
}
