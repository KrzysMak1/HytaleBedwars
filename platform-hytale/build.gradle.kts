plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":storage"))
    implementation(project(":api"))
    implementation("org.yaml:snakeyaml:2.2")
}
