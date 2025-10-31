package uk.firedev.firefly.modules.protection;

import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;

import java.util.List;

public class ProtectionConfig extends ConfigBase {

    private static ProtectionConfig instance;

    private ProtectionConfig() {
        super("modules/protection.yml", "modules/protection.yml", Firefly.getInstance());
        withDefaultUpdaterSettings();
    }

    public static ProtectionConfig getInstance() {
        if (instance == null) {
            instance = new ProtectionConfig();
        }
        return instance;
    }

    // Getters

    public boolean isAmethystProtectEnabled() {
        return getConfig().getBoolean("amethyst-protection.enabled", false);
    }

    public boolean isLootChestProtectionEnabled() {
        return getConfig().getBoolean("loot-chest-protection.enabled", false);
    }

    // AMETHYST PROTECTION

    public ComponentMessage getAmethystProtectEnabledMessage() {
        return getComponentMessage("amethyst-protection.messages.enabled", "<color:#F0E68C>Enabled Amethyst Protection</color>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getAmethystProtectDisabledMessage() {
        return getComponentMessage("amethyst-protection.messages.disabled", "<color:#F0E68C>Disabled Amethyst Protection</color>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getAmethystProtectProtectedMessage() {
        return getComponentMessage(
            "amethyst-protection.messages.protected",
            List.of(
                "",
                "<color:#F0E68C>AmethystProtect has protected this block.</color>",
                "",
                "<red>You can disable this with <click:run_command:'/amethystprotect'><u>/amethystprotect</u></click></red>",
                ""
            )
        ).replace(MessageConfig.getInstance().getPrefixReplacer());
    }

}
