package uk.firedev.skylight.config;

import net.kyori.adventure.text.Component;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.skylight.Skylight;

import java.util.HashMap;
import java.util.Map;

public class MessageConfig extends uk.firedev.daisylib.Config {

    private static MessageConfig instance = null;

    private MessageConfig() {
        super("messages.yml", Skylight.getInstance(), true);
    }

    public static MessageConfig getInstance() {
        if (instance == null) {
            instance = new MessageConfig();
        }
        return instance;
    }

    public ComponentReplacer getPrefixReplacer() {
        return new ComponentReplacer().addReplacement("prefix", getPrefix().getMessage());
    }

    // GENERAL MESSAGES

    public ComponentMessage getPrefix() {
        return new ComponentMessage(getConfig(), "messages.prefix", "<gray>[Skylight]</gray> ");
    }

    public ComponentMessage getPlayerNotFoundMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.player-not-found", "<red>Player not found.");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    // MAIN COMMAND MESSAGES

    public ComponentMessage getMainCommandReloadedMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.main-command.reloaded", "<color:#F0E68C>Successfully reloaded the plugin");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getMainCommandUsageMessage() {
        Map<String, String> commandMap = Map.of(
                "/skylight reload", "Reloads the plugin.",
                "/skylight modules", "Are modules enabled?"
        );
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.main-command.usage.header", "{prefix}<color:#F0E68C>Command Usage:");
        ComponentMessage command = new ComponentMessage(getConfig(), "messages.main-command.usage.command", "{prefix}<aqua>{command} <color:#F0E68C>- {description}");
        // Add command values to message
        for (String key : commandMap.keySet()) {
            String value = commandMap.get(key);
            ComponentReplacer replacer = new ComponentReplacer().addReplacements(
                    "command", key,
                    "description", value
            );
            message = message.append(Component.newline()).append(command.duplicate().applyReplacer(replacer));
        }
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getMainCommandModulesMessage() {
        ComponentMessage message = new ComponentMessage(
                """
                {prefix}<color:#F0E68C>Kits:</color> {kitsEnabled}
                {prefix}<color:#F0E68C>Amethyst Protection:</color> {apEnabled}
                {prefix}<color:#F0E68C>Loot Chest Protection:</color> {lcpEnabled}
                {prefix}<color:#F0E68C>Elevators:</color> {elevatorsEnabled}
                {prefix}<color:#F0E68C>Titles:</color> {titlesEnabled}
                {prefix}<color:#F0E68C>Nicknames:</color> {nicknamesEnabled}"""
        );
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    // TITLE MESSAGES

    public ComponentMessage getTitlePrefixSetMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.title.prefix-set", "<color:#F0E68C>Applied Prefix {prefix}.</color>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getTitlePrefixRemovedMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.title.prefix-removed", "<red>Removed Current Prefix.</red>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getTitleSuffixSetMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.title.suffix-set", "<color:#F0E68C>Applied Suffix {suffix}.</color>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getTitleSuffixRemovedMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.title.suffix-removed", "<red>Removed Current Suffix.</red>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    // AMETHYST PROTECTION MESSAGES

    public ComponentMessage getAmethystProtectEnabledMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.amethyst-protection.enabled", "<color:#F0E68C>Enabled Amethyst Protection</color>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getAmethystProtectDisabledMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.amethyst-protection.disabled", "<color:#F0E68C>Disabled Amethyst Protection</color>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getAmethystProtectProtectedMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.amethyst-protection.protected", "\n<color:#F0E68C>AmethystProtect has protected this block.</color>\n<red>You can disable this with <click:run_command:'/amethystprotect'><u>/amethystprotect</u></click></red>\n");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    // NICKNAME MESSAGES

    public ComponentMessage getNicknameRealHoverMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.nicknames.real-name-hover", "<color:#F0E68C>Real Username:</color> <white>{username}</white>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getNicknameCommandOwnNicknameMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.nicknames.command.own-nickname", "<color:#F0E68C>Your Current Nickname is:</color> <white>{nickname}</white>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getNicknameCommandTooLongMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.nicknames.command.too-long", "<red>That nickname is too long! Maximum length {max-length}</red>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getNicknameCommandTooShortMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.nicknames.command.too-short", "<red>That nickname is too short! Minimum length {min-length}</red>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getNicknameCommandBlacklistedMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.nicknames.command.blacklisted", "<red>That name is blacklisted! Please try something else.</red>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getNicknameCommandNoUniqueMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.nicknames.command.no-unique-name", "<red>You don't have permission to set a unique nickname!</red>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getNicknameCommandSetOwnNicknameMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.nicknames.command.set-own-nickname", "<color:#F0E68C>Set your nickname to</color> <white>{nickname}</white>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getNicknameCommandRemovedNicknameMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.nicknames.command.removed-nickname", "<color:#F0E68C>Removed your nickname.</color>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getNicknameCommandAdminUsageMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.nicknames.command.admin.usage", "<red>Usage: /nicknameadmin <player> <nickname>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getNicknameCommandAdminSetNicknameMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.nicknames.command.admin.set-others-nickname", "<color:#F0E68C>Set {target}'s nickname to</color> <white>{nickname}</white>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getNicknameCommandAdminRemovedNicknameMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.nicknames.command.admin.removed-others-nickname", "<color:#F0E68C>Removed {player}'s nickname.</color>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getNicknameCommandCheckInfoMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.nicknames.command.check.check-info", "<color:#F0E68C>{player}'s Nickname:</color> <white>{nickname}</white>");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

}
