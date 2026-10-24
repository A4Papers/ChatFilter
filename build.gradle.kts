plugins {
    java
    id("com.gradleup.shadow") version "9.4.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

group = "a4.papers.chatfilter"
version = "2.0.16"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    implementation("com.github.Anon8281:UniversalScheduler:0.1.7")
    testImplementation("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
}

tasks {
    test {
        useJUnitPlatform()
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        archiveClassifier.set("") // Produces plain jar
        // Use runtimeClasspath instead of implementation to fix Gradle 8+ issue
        configurations = listOf(project.configurations.runtimeClasspath.get())
        relocate(
            "com.github.Anon8281.universalScheduler",
            "a4.papers.chatfilter.universalScheduler"
        )
    }

    build {
        dependsOn(shadowJar)
    }
}
