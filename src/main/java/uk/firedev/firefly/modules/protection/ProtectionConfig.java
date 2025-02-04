package uk.firedev.firefly.modules.protection;

import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

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
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "amethyst-protection.messages.enabled", "<color:#F0E68C>Enabled Amethyst Protection</color>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getAmethystProtectDisabledMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "amethyst-protection.messages.disabled", "<color:#F0E68C>Disabled Amethyst Protection</color>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getAmethystProtectProtectedMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "amethyst-protection.messages.protected", "\n<color:#F0E68C>AmethystProtect has protected this block.</color>\n<red>You can disable this with <click:run_command:'/amethystprotect'><u>/amethystprotect</u></click></red>\n");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

}
