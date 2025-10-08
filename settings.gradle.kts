rootProject.name = "Firefly"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("daisylib", "uk.firedev:DaisyLib:2.6.1-SNAPSHOT")
            library("vault", "com.github.MilkBowl:VaultAPI:1.7.1")
            library("paper-api", "io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
            library("placeholderapi", "me.clip:placeholderapi:2.11.6")
            library("miniplaceholders", "io.github.miniplaceholders:miniplaceholders-api:2.2.3")

            plugin("shadow", "com.gradleup.shadow").version("9.0.0")
            plugin("plugin-yml", "net.minecrell.plugin-yml.paper").version("0.6.0")
        }
    }
}