package uk.firedev.firefly.modules.playtime;

import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;

public class PlaytimeConfig extends ConfigBase {

    private static PlaytimeConfig instance;

    private PlaytimeConfig() {
        super("modules/playtime.yml", "modules/playtime.yml", Firefly.getInstance());
        init();
    }

    public static PlaytimeConfig getInstance() {
        if (instance == null) {
            instance = new PlaytimeConfig();
        }
        return instance;
    }

    public ComponentMessage getCheckPlaytimeMessage() {
        return getComponentMessage("messages.command.check-playtime", "<color:#F0E68C>{player}'s Playtime:</color> <white>{playtime}</white>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getAdminSetPlaytimeMessage() {
        return getComponentMessage("messages.command.admin.set-playtime", "<#F0E68C>Your playtime has been set to <white>{playtime}").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getAdminSetPlaytimeSenderMessage() {
        return getComponentMessage( "messages.command.admin.set-playtime-sender", "<#F0E68C>Set {target}'s playtime to <white>{playtime}").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

}
