package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Config;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.daisylib.message.string.StringMessage;
import uk.firedev.daisylib.utils.LocationHelper;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

import java.io.IOException;

public class TeleportConfig extends Config {

    private static TeleportConfig instance;

    private TeleportConfig() {
        super("modules/teleport.yml", "modules/teleport.yml", Firefly.getInstance(), true);
    }

    public static TeleportConfig getInstance() {
        if (instance == null) {
            instance = new TeleportConfig();
        }
        return instance;
    }

    // General messages

    public ComponentMessage getTeleportedMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.teleported",
                "<color:#F0E68C>You have teleported to {target-location}!"
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getLocationInvalidMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.location-invalid",
                "<red>That location is not valid!"
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    // Spawn command related things

    public void setSpawnLocation(boolean firstSpawn, @NotNull Location location) {
        String spawnLocKey = firstSpawn ? "spawn.first-spawn-location" : "spawn.spawn-location";
        LocationHelper.addToConfig(getConfig(), spawnLocKey, location);
        try {
            getConfig().save();
            TeleportManager.getInstance().refreshSpawnLocations();
        } catch (IOException exception) {
            Loggers.warn(Firefly.getInstance().getComponentLogger(), "Failed to save spawn location to config!", exception);
        }
    }

    public Location getSpawnLocation(boolean firstSpawn) {
        String spawnLocKey = firstSpawn ? "spawn.first-spawn-location" : "spawn.spawn-location";
        String invalidLocation = firstSpawn ? "The first spawn location is not valid!" : "The spawn location is not valid!";

        Location location = LocationHelper.getFromConfig(getConfig(), spawnLocKey);
        if (location == null) {
            Loggers.warn(Firefly.getInstance().getComponentLogger(), invalidLocation);
            return null;
        }
        return location;
    }

    public boolean isSpawnOnJoin() {
        return getConfig().getBoolean("spawn.spawn-on-join");
    }

    public ComponentMessage getSpawnSentPlayerToSpawnMessage(@NotNull Player targetPlayer) {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.spawn.sent-player-to-spawn",
                "<color:#F0E68C>Sent {target} to spawn."
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer())
                .replace("target", targetPlayer.getName());
    }

    public ComponentMessage getSpawnTeleportedToSpawnMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.spawn.teleported-to-spawn",
                "<color:#F0E68C>You have been teleported to spawn."
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getSpawnSetSpawnMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.spawn.spawn-set",
                "<color:#F0E68C>Set the spawn location to your current location."
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getSpawnSetFirstSpawnMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.spawn.first-spawn-set",
                "<color:#F0E68C>Set the first spawn location to your current location."
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    // TPA related things

    public int getTpaMaximumCachedRequests() {
        return getConfig().getInt("tpa.cached-requests", 3);
    }

    public int getTpaRequestExpiry() {
        return getConfig().getInt("tpa.request-expiry", 15);
    }

    public ComponentMessage getTpaCannotRequestSelfMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.tpa.cannot-request-self",
                "<red>You cannot send a request to yourself!"
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getTpaTargetFlyingMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.tpa.target-flying",
                "<red>Cannot teleport because the target is flying!"
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getTpaToRequestSenderMessage(@NotNull Player target) {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.tpa.to.request-sender",
                "<color:#F0E68C>Requested to teleport to {target}"
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer())
                .replace("target", target.getName());
    }

    public ComponentMessage getTpaToRequestTargetMessage(@NotNull Player sender) {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.tpa.to.request-target",
                "<color:#F0E68C>{player} wants to teleport to you! {accept} {deny}"
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer())
                .replace("player", sender.getName());
    }

    public ComponentMessage getTpaHereRequestSenderMessage(@NotNull Player target) {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.tpa.here.request-sender",
                "<color:#F0E68C>Invited {target} to teleport to you"
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer())
                .replace("target", target.getName());
    }

    public ComponentMessage getTpaHereRequestTargetMessage(@NotNull Player sender) {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.tpa.here.request-target",
                "<color:#F0E68C>{player} wants you to teleport to them! {accept} {deny}"
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer())
                .replace("player", sender.getName());
    }

    public ComponentMessage getTpaRequestAcceptedTargetMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.tpa.accepted-target",
                "<color:#F0E68C>Teleport request accepted!"
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getTpaRequestAcceptedTeleporterMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.tpa.accepted-teleporter",
                "<color:#F0E68C>Teleport request accepted! Teleporting..."
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getTpaRequestDeniedSenderMessage(@NotNull Player target) {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.tpa.request-denied-sender",
                "<color:#F0E68C>{target} has denied your teleport request!"
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer())
                .replace("target", target.getName());
    }

    public ComponentMessage getTpaRequestDeniedTargetMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.tpa.request-denied-target",
                "<color:#F0E68C>Denied the teleport request!"
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getTpaAcceptClickMessage(@NotNull Player sender) {
        StringMessage message = StringMessage.fromConfig(
                getConfig(),
                "messages.command.tpa.accept-click",
                "<green><click:run_command:'/tpaccept {sender}'>[Accept]"
        );
        message.replace("sender", sender.getName());
        return message.toComponentMessage();
    }

    public ComponentMessage getTpaDenyClickMessage(@NotNull Player sender) {
        StringMessage message = StringMessage.fromConfig(
                getConfig(),
                "messages.command.tpa.deny-click",
                "<red><click:run_command:'/tpdeny {sender}'>[Deny]"
        );
        message.replace("sender", sender.getName());
        return message.toComponentMessage();
    }

    // /back related things

    public boolean shouldBackSaveDeath() {
        return getConfig().getBoolean("back.save-death", true);
    }

    public ComponentMessage getBackTeleportedMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.back.teleported-back",
                "<color:#F0E68C>You have been teleported to your last saved location."
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getBackTeleportedSenderMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.back.teleported-back-sender",
                "<color:#F0E68C>You have teleported {target} to their last saved location."
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getDBackTeleportedMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.dback.teleported-back",
                "<color:#F0E68C>You have been teleported to your last death location."
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getDBackTeleportedSenderMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(
                getConfig(),
                "messages.command.dback.teleported-back-sender",
                "<color:#F0E68C>You have teleported {target} to their last death location."
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

}
