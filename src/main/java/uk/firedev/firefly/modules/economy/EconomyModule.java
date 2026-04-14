package uk.firedev.firefly.modules.economy;

import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.data.type.Fire;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.util.Loggers;
import uk.firedev.daisylib.util.VaultManager;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.economy.command.BalanceCommand;
import uk.firedev.firefly.modules.economy.command.MoneyCommand;
import uk.firedev.firefly.placeholders.Placeholders;

import javax.xml.crypto.Data;

public class EconomyModule implements Module {

    private static EconomyModule instance = null;

    public static final String BALANCE_PERMISSION = "firefly.command.balance";
    public static final String BALTOP_PERMISSION = "firefly.command.baltop";
    public static final String MONEY_PERMISSION = "firefly.command.money";

    private EconomyModule() {}

    public static EconomyModule getInstance() {
        if (instance == null) {
            instance = new EconomyModule();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "Economy";
    }

    @Override
    public boolean isConfigEnabled() {
        return ModuleConfig.getInstance().isModuleEnabled("economy");
    }

    @Override
    public void init() {
        Firefly plugin = Firefly.getInstance();

        EconomyDatabase.getInstance().register(plugin.getDatabase());
        Bukkit.getServicesManager().register(
            Economy.class,
            new FireflyEconomy(),
            plugin,
            ServicePriority.Highest
        );
        Loggers.warn(plugin.getComponentLogger(), "Registered FireflyEconomy.");
        VaultManager.getInstance().load();

        new BalanceCommand().initCommand();
        new MoneyCommand().initCommand();
    }

    @Override
    public void reload() {
        EconomyConfig.getInstance().reload();
    }

    @Override
    public void unload() {}

    @Override
    public void registerPlaceholders() {
        Placeholders.manageProvider(provider ->
            provider.addAudiencePlaceholder("player_balance", audience -> {
                if (!isConfigEnabled()) {
                    return MessageConfig.getInstance().getFeatureDisabledMessage().toSingleMessage().get();
                }
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                return Component.text(PlayerData.playerData(player.getUniqueId()).getBalance());
            }));
    }

}
