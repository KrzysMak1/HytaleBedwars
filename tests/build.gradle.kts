plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation(project(":core"))
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.test {
    useJUnitPlatform()
}
