import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.plugin.yml)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/FireML/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.daisylib)
    compileOnly(libs.vault)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.miniplaceholders)
}

group = "uk.firedev"
version = "1.4.0-SNAPSHOT"
description = "A collection of helpful server features."
java.sourceCompatibility = JavaVersion.VERSION_21

paper {
    name = project.name
    version = project.version.toString()
    main = "uk.firedev.firefly.Firefly"
    apiVersion = "1.21.10"
    author = "FireML"
    description = project.description.toString()

    serverDependencies {
        register("Vault") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("DaisyLib") {
            required = true
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

    permissions {
        register("firefly.admin")
        register("firefly.command.nickname.bypass.blacklist")
        register("firefly.command.nickname.bypass.length")
        register("firefly.command.nickname.colors")
        register("firefly.command.nickname.unique")
    }

}

publishing {
    repositories {
        maven {
            url = uri("https://repo.codemc.io/repository/FireML/")

            val mavenUsername = System.getenv("JENKINS_USERNAME")
            val mavenPassword = System.getenv("JENKINS_PASSWORD")

            if (mavenUsername != null && mavenPassword != null) {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["shadow"])
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {

        archiveBaseName.set(project.name)
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("")
        
        //manifest {
        //    attributes["paperweight-mappings-namespace"] = "spigot"
        //}
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
