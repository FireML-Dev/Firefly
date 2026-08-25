package uk.firedev.firefly.modules.economy;

import org.bukkit.plugin.ServicePriority;
import uk.firedev.daisylib.external.vault.VaultWrapper;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.modules.economy.command.BalanceCommand;
import uk.firedev.firefly.modules.economy.command.BaltopCommand;
import uk.firedev.firefly.modules.economy.command.MoneyCommand;
import uk.firedev.firefly.modules.economy.command.PayCommand;
import uk.firedev.firefly.modules.economy.placeholders.PlayerBalancePlaceholder;
import uk.firedev.firefly.placeholders.FireflyPlaceholders;

public class EconomyModule implements Module {

    private static EconomyModule instance = null;

    public static final String BALANCE_PERMISSION = "firefly.command.balance";
    public static final String BALTOP_PERMISSION = "firefly.command.baltop";
    public static final String MONEY_PERMISSION = "firefly.command.money";
    public static final String PAY_PERMISSION = "firefly.command.pay";

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
        new FireflyEconomy().register(plugin, ServicePriority.Highest);
        plugin.getLogging().info("Registered FireflyEconomy.");
        VaultWrapper.get().load();

        new BalanceCommand().initCommand();
        new MoneyCommand().initCommand();
        new PayCommand().initCommand();
        new BaltopCommand().initCommand();

        registerPlaceholders();
    }

    @Override
    public void reload() {
        EconomyConfig.getInstance().reload();
    }

    @Override
    public void unload() {}

    private void registerPlaceholders() {
        FireflyPlaceholders.get().add(new PlayerBalancePlaceholder(this));
    }

}
