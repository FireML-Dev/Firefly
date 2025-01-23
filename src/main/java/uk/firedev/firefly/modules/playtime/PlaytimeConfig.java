package uk.firedev.firefly.modules.playtime;

import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

public class PlaytimeConfig extends ConfigBase {

    private static PlaytimeConfig instance;

    private PlaytimeConfig() {
        super("modules/playtime.yml", "modules/playtime.yml", Firefly.getInstance());
        withDefaultUpdaterSettings();
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

    public ComponentMessage getCheckPlaytimeMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.command.check-playtime", "<color:#F0E68C>{player}'s Playtime:</color> <white>{playtime}</white>");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getAdminSetPlaytimeMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.command.admin.set-playtime", "<#F0E68C>Your playtime has been set to <white>{playtime}");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getAdminSetPlaytimeSenderMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.command.admin.set-playtime-sender", "<#F0E68C>Set {target}'s playtime to <white>{playtime}");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

}
