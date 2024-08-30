package uk.firedev.firefly.modules.nickname;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Manager;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.modules.nickname.command.NicknameAdminCommand;
import uk.firedev.firefly.modules.nickname.command.NicknameCheckCommand;
import uk.firedev.firefly.modules.nickname.command.NicknameCommand;
import uk.firedev.firefly.modules.playtime.PlaytimeDatabase;
import uk.firedev.firefly.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NicknameManager implements Manager {

    private static NicknameManager instance = null;

    private boolean loaded;
    private Map<UUID, String> nicknameMap = new ConcurrentHashMap<>();

    private NicknameManager() {}

    public static NicknameManager getInstance() {
        if (instance == null) {
            instance = new NicknameManager();
        }
        return instance;
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        NicknameConfig.getInstance().reload();
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering Nickname Commands");
        NicknameCommand.getInstance().register(Firefly.getInstance());
        NicknameAdminCommand.getInstance().register(Firefly.getInstance());
        NicknameCheckCommand.getInstance().register(Firefly.getInstance());
        NicknameDatabase.getInstance().register(Database.getInstance());
        populateNicknameMap();
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        NicknameConfig.getInstance().reload();
        populateNicknameMap();
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        CommandAPI.unregister(NicknameCheckCommand.getInstance().getName());
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    // Nickname Management

    /**
     * Gets a player's nickname as an Adventure Component
     * @param player The player whose nickname to retrieve
     * @return The player's nickname as an Adventure Component, or null if the manager is not loaded.
     */
    public Component getNickname(@NotNull OfflinePlayer player) {
        if (!isLoaded()) {
            return null;
        }
        String savedName = getNicknameMap().get(player.getUniqueId());
        if (savedName == null || savedName.isEmpty()) {
            savedName = Objects.requireNonNull(player.getName());
        }
        Component component = StringUtils.getComponentLegacy(savedName);
        ComponentMessage componentMessage = ComponentMessage.of(component);
        if (componentMessage.matchesString(Objects.requireNonNull(player.getName()))) {
            ComponentReplacer replacer = new ComponentReplacer().addReplacements("username", player.getName());
            component = component.hoverEvent(
                    HoverEvent.hoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            NicknameConfig.getInstance().getRealNameHoverMessage().applyReplacer(replacer).getMessage()
                    )
            );
        }
        return component;
    }

    /**
     * Gets a player's nickname as a Legacy String
     * @param player The player whose nickname to retrieve
     * @return The player's nickname as a Legacy String, or null if the manager is not loaded.
     */
    public String getStringNickname(@NotNull OfflinePlayer player) {
        if (!isLoaded()) {
            return null;
        }
        return LegacyComponentSerializer.legacySection().serialize(getNickname(player));
    }

    /**
     * Sets a player's nickname via an Adventure Component
     * @param player The player to set
     * @param nickname The nickname to set as an Adventure Component
     */
    public boolean setNickname(@NotNull OfflinePlayer player, @NotNull Component nickname) {
        if (!isLoaded()) {
            return false;
        }
        return setStringNickname(player, LegacyComponentSerializer.legacySection().serialize(nickname));
    }

    /**
     * Sets a player's nickname via a Legacy String
     * @param player The player to set
     * @param nickname The nickname to set, as a Legacy String
     */
    public boolean setStringNickname(@NotNull OfflinePlayer player, @NotNull String nickname) {
        if (!isLoaded() || getNicknameMap().containsKey(player.getUniqueId())) {
            return false;
        }
        nicknameMap.put(player.getUniqueId(), nickname);
        return true;
    }

    /**
     * Removes a player's custom nickname.
     * @param player The player whose nickname to remove.
     */
    public void removeNickname(@NotNull OfflinePlayer player) {
        if (!isLoaded()) {
            return;
        }
        if (!nicknameMap.containsKey(player.getUniqueId())) {
            return;
        }
        nicknameMap.put(player.getUniqueId(), "");
    }

    public void populateNicknameMap() {
        nicknameMap = new HashMap<>(NicknameDatabase.getInstance().getNicknames());
    }

    public @NotNull Map<UUID, String> getNicknameMap() {
        if (nicknameMap == null) {
            nicknameMap = new HashMap<>();
        }
        return Map.copyOf(nicknameMap);
    }

    public void saveAllNicknames() {
        getNicknameMap().forEach(NicknameDatabase.getInstance()::saveToDatabase);
    }

}
