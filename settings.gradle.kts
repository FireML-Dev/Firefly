rootProject.name = "Firefly"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("paper-api", "io.papermc.paper:paper-api:26.2.build.+")
            library("daisylib", "uk.firedev:DaisyLib:4.0-SNAPSHOT")
            library("vault", "com.github.MilkBowl:VaultAPI:1.7.1")
            library("luckperms", "net.luckperms:api:5.5")
            library("placeholderapi", "me.clip:placeholderapi:2.11.6")
            library("evenmorefish", "com.oheers.evenmorefish:even-more-fish-api:2.4.5")

            library("triumphgui", "dev.triumphteam:triumph-gui-paper:3.1.13")
            library("customblockdata", "com.jeff-media:custom-block-data:2.2.8")

            plugin("shadow", "com.gradleup.shadow").version("9.0.0")
            plugin("plugin-yml", "de.eldoria.plugin-yml.paper").version("0.9.0")
        }
    }
}