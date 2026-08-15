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
    compileOnly(libs.vault)
    compileOnly(libs.luckperms)
    compileOnly(libs.placeholderapi)

    implementation(libs.daisylib)
    implementation(libs.triumphgui)
    implementation(libs.customblockdata)
}

group = "uk.firedev"
version = properties["project-version"] as String
description = "A collection of helpful server features."
java.sourceCompatibility = JavaVersion.VERSION_25

paper {
    name = project.name
    version = project.version.toString()
    main = "uk.firedev.firefly.Firefly"
    apiVersion = "26.2"
    author = "FireML"
    description = project.description.toString()

    serverDependencies {
        register("LuckPerms") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Vault") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("PlaceholderAPI") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
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

        relocate("uk.firedev.daisylib", "uk.firedev.firefly.libs.daisylib")
        relocate("com.jeff_media.customblockdata", "uk.firedev.firefly.libs.customblockdata")
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    generatePaperPluginDescription {
        useGoogleMavenCentralProxy()
    }
}
