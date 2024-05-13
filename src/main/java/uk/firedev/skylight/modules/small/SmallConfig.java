package uk.firedev.skylight.modules.small;

import uk.firedev.daisylib.Config;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.config.MessageConfig;

public class SmallConfig extends Config {

    private static SmallConfig instance;

    private SmallConfig() {
        super("small.yml", Skylight.getInstance(), true, true);
    }

    public static SmallConfig getInstance() {
        if (instance == null) {
            instance = new SmallConfig();
        }
        return instance;
    }

    // AMETHYST PROTECTION

    public ComponentMessage getAmethystProtectEnabledMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.amethyst-protection.enabled", "<color:#F0E68C>Enabled Amethyst Protection</color>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getAmethystProtectDisabledMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.amethyst-protection.disabled", "<color:#F0E68C>Disabled Amethyst Protection</color>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getAmethystProtectProtectedMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.amethyst-protection.protected", "\n<color:#F0E68C>AmethystProtect has protected this block.</color>\n<red>You can disable this with <click:run_command:'/amethystprotect'><u>/amethystprotect</u></click></red>\n");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

}
