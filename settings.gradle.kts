rootProject.name = "Firefly"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("paper-api", "io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
            library("daisylib", "uk.firedev:DaisyLib:2.8.0-SNAPSHOT")
            library("vault", "net.milkbowl.vault:VaultUnlockedAPI:2.16")
            library("placeholderapi", "me.clip:placeholderapi:2.11.6")
            library("miniplaceholders", "io.github.miniplaceholders:miniplaceholders-api:2.2.3")

            plugin("shadow", "com.gradleup.shadow").version("9.0.0")
            plugin("plugin-yml", "de.eldoria.plugin-yml.paper").version("0.8.0")
        }
    }
}