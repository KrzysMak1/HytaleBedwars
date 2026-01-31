plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core"))
    implementation("org.xerial:sqlite-jdbc:3.45.2.0")
    implementation("com.mysql:mysql-connector-j:9.0.0")
}
