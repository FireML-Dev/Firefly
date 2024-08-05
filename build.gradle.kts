import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.plugin.yml)
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.firedev.uk/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.daisylib)
    compileOnly(libs.vault)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.miniplaceholders)
    compileOnly(libs.cmi.api)
}

group = "uk.firedev"
version = "1.0.2-SNAPSHOT"
description = "A collection of helpful server features."
java.sourceCompatibility = JavaVersion.VERSION_21

paper {
    name = project.name
    version = project.version.toString()
    main = "uk.firedev.firefly.Firefly"
    apiVersion = "1.21"
    author = "FireML"
    description = project.description.toString()
    foliaSupported = true

    serverDependencies {
        register("Vault") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("DaisyLib") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("CMI") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("PlaceholderAPI") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("MiniPlaceholders") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

publishing {
    repositories {
        maven {
            name = "firedevRepo"

            // Repository settings
            var repoUrlString = "https://repo.firedev.uk/repository/maven-"
            repoUrlString += if (project.version.toString().endsWith("-SNAPSHOT")) {
                "snapshots/"
            } else {
                "releases/"
            }
            url = uri(repoUrlString)

            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "spigot"
        }
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
