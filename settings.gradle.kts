rootProject.name = "seal-root"
include(":test-app", ":test-library")
includeBuild("seal") {
    dependencySubstitution {
        substitute(module("me.2bab:seal"))
            .with(project(":plugin"))
    }
}