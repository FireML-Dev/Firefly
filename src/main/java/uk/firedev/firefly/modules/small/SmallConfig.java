package uk.firedev.firefly.modules.small;

import uk.firedev.daisylib.Config;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

public class SmallConfig extends Config {

    private static SmallConfig instance;

    private SmallConfig() {
        super("modules/small.yml", "modules/small.yml", Firefly.getInstance(), true);
    }

    public static SmallConfig getInstance() {
        if (instance == null) {
            instance = new SmallConfig();
        }
        return instance;
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
