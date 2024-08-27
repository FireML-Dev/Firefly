package uk.firedev.firefly.modules.playtime;

import uk.firedev.daisylib.Config;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

public class PlaytimeConfig extends Config {

    private static PlaytimeConfig instance;

    private PlaytimeConfig() {
        super("playtime.yml", "playtime.yml", Firefly.getInstance(), false);
    }

    public static PlaytimeConfig getInstance() {
        if (instance == null) {
            instance = new PlaytimeConfig();
        }
        return instance;
    }

    public long getSaveInterval() {
        return getConfig().getLong("save-interval");
    }

    public ComponentMessage getCommandCheckPlaytimeMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.command.check-playtime", "<color:#F0E68C>{player}'s Playtime:</color> <white>{playtime}</white>");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getCommandAdminSetPlaytimeMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.command.admin.set-playtime", "<color:#F0E68C>Set {target}'s playtime to</color> <white>{playtime}</white>");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

}
