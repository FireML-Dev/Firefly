rootProject.name = "Firefly"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("paper-api", "io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
            library("daisylib", "uk.firedev:DaisyLib:2.2.0-SNAPSHOT")
            library("vault", "com.github.MilkBowl:VaultAPI:1.7")
            library("placeholderapi", "me.clip:placeholderapi:2.11.6")
            library("miniplaceholders", "io.github.miniplaceholders:miniplaceholders-api:2.2.3")

            plugin("shadow", "com.gradleup.shadow").version("8.3.5")
            plugin("plugin-yml", "net.minecrell.plugin-yml.paper").version("0.6.0")
        }
    }
}