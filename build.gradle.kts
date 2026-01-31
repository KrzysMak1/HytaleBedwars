import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22" apply false
    id("com.diffplug.spotless") version "6.25.0" apply false
}

allprojects {
    group = "com.hytale.bedwars"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.diffplug.spotless")

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "21"
    }

    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    extensions.configure<SpotlessExtension> {
        kotlin {
            ktlint("1.1.1")
        }
    }
}
