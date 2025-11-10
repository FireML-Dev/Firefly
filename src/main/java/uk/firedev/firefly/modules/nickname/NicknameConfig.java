package uk.firedev.firefly.modules.nickname;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;

public class NicknameConfig extends ConfigBase {

    private static NicknameConfig instance;

    private NicknameConfig() {
        super("modules/nicknames.yml", "modules/nicknames.yml", Firefly.getInstance());
        withDefaultUpdaterSettings();
    }

    public static NicknameConfig getInstance() {
        if (instance == null) {
            instance = new NicknameConfig();
        }
        return instance;
    }

    public boolean isTooLong(@NotNull String nickname) {
        return nickname.length() > getMaxLength();
    }

    public int getMaxLength() {
        return getConfig().getInt("settings.max-length");
    }

    public boolean isTooShort(@NotNull String nickname) {
        return nickname.length() < getMinLength();
    }

    public int getMinLength() {
        return getConfig().getInt("settings.min-length");
    }

    public boolean isBlacklisted(@NotNull String nickname) {
        return getConfig().getStringList("nicknames.blacklisted-names")
            .stream()
            .anyMatch(string -> string.equalsIgnoreCase(nickname));
    }

    public ComponentMessage getRealNameHoverMessage() {
        return getComponentMessage("messages.real-name-hover", "<color:#F0E68C>Real Username:</color> <white>{username}</white>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCommandTooLongMessage() {
        return getComponentMessage("messages.command.too-long", "<red>That nickname is too long! Maximum length {max-length}</red>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCommandTooShortMessage() {
        return getComponentMessage("messages.command.too-short", "<red>That nickname is too short! Minimum length {min-length}</red>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCommandBlacklistedMessage() {
        return getComponentMessage("messages.command.blacklisted", "<red>That name is not allowed! Please try something else.</red>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCommandNoUniqueMessage() {
        return getComponentMessage("messages.command.no-unique-name", "<red>You don't have permission to set a unique nickname!</red>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCommandSetOwnNicknameMessage() {
        return getComponentMessage("messages.command.set-own-nickname", "<color:#F0E68C>Set your nickname to</color> <white>{nickname}</white>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCommandRemovedNicknameMessage() {
        return getComponentMessage("messages.command.removed-nickname", "<color:#F0E68C>Removed your nickname.</color>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCommandAdminSetNicknameMessage() {
        return getComponentMessage("messages.command.admin.set-others-nickname", "<color:#F0E68C>Set {target}'s nickname to</color> <white>{nickname}</white>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCommandAdminRemovedNicknameMessage() {
        return getComponentMessage("messages.command.admin.removed-others-nickname", "<color:#F0E68C>Removed {player}'s nickname.</color>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCommandCheckInfoMessage() {
        return getComponentMessage("messages.command.check-info", "<color:#F0E68C>{player}'s Nickname:</color> <white>{nickname}</white>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

}
