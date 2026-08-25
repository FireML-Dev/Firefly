package uk.firedev.firefly.modules.playtime;

import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.config.BasicConfig;
import uk.firedev.daisylib.messages.message.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

public class PlaytimeConfig extends BasicConfig {

    private static PlaytimeConfig instance;

    private PlaytimeConfig() {
        super("modules/playtime.yml", "modules/playtime.yml", Firefly.getInstance());
    }

    public static PlaytimeConfig getInstance() {
        if (instance == null) {
            instance = new PlaytimeConfig();
        }
        return instance;
    }

    public ComponentMessage<?, ?>  getCheckPlaytimeMessage() {
        return getComponentMessage("messages.command.check-playtime", "<color:#F0E68C>{player}'s Playtime:</color> <white>{playtime}</white>");
    }

    public ComponentMessage<?, ?>  getAdminSetPlaytimeMessage() {
        return getComponentMessage("messages.command.admin.set-playtime", "<#F0E68C>Your playtime has been set to <white>{playtime}");
    }

    public ComponentMessage<?, ?>  getAdminSetPlaytimeSenderMessage() {
        return getComponentMessage( "messages.command.admin.set-playtime-sender", "<#F0E68C>Set {target}'s playtime to <white>{playtime}");
    }

    @Override
    public ComponentMessage<?, ?> getComponentMessage(@NonNull String path, @NonNull Object def) {
        return super.getComponentMessage(path, def).replace("{prefix}", MessageConfig.getInstance().getPrefix());
    }

}
