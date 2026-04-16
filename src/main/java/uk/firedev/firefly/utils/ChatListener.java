package uk.firedev.firefly.utils;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import uk.firedev.firefly.config.MainConfig;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.ModuleManager;
import uk.firedev.firefly.modules.nickname.NicknameModule;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (!NicknameModule.getInstance().isConfigEnabled()) {
            return;
        }
        PlayerData data = PlayerData.playerData(event.getPlayer().getUniqueId());
        if (data.getRawNickname() != null) {
            event.getPlayer().displayName(data.getNickname());
        }
    }

}
