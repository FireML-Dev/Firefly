package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.utils.LocationHelper;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.messagelib.message.ComponentMessage;

public class TeleportConfig extends ConfigBase {

    private static TeleportConfig instance;

    private TeleportConfig() {
        super("modules/teleport.yml", "modules/teleport.yml", Firefly.getInstance());
        withDefaultUpdaterSettings();
    }

    public static TeleportConfig getInstance() {
        if (instance == null) {
            instance = new TeleportConfig();
        }
        return instance;
    }

    // General messages

    public ComponentMessage getTeleportedMessage() {
        return getComponentMessage("messages.teleported", "<color:#F0E68C>You have teleported to {target-location}!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getLocationInvalidMessage() {
        return getComponentMessage("messages.location-invalid", "<red>That location is not valid!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    // Spawn command related things

    public void setSpawnLocation(boolean firstSpawn, @NotNull Location location) {
        String spawnLocKey = firstSpawn ? "spawn.first-spawn-location" : "spawn.spawn-location";
        LocationHelper.addToConfig(getConfig(), spawnLocKey, location);
        save();
        TeleportModule.getInstance().refreshSpawnLocations();
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
        return getComponentMessage("messages.command.spawn.sent-player-to-spawn", "<color:#F0E68C>Sent {target} to spawn.")
            .replace(MessageConfig.getInstance().getPrefixReplacer())
            .replace("{target}", targetPlayer.getName());
    }

    public ComponentMessage getSpawnTeleportedToSpawnMessage() {
        return getComponentMessage("messages.command.spawn.teleported-to-spawn", "<color:#F0E68C>You have been teleported to spawn.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getSpawnSetSpawnMessage() {
        return getComponentMessage("messages.command.spawn.spawn-set", "<color:#F0E68C>Set the spawn location to your current location.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getSpawnSetFirstSpawnMessage() {
        return getComponentMessage("messages.command.spawn.first-spawn-set", "<color:#F0E68C>Set the first spawn location to your current location.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public int getSpawnWarmupSeconds() {
        return getConfig().getInt("spawn.warmup", 0);
    }

    // TPA related things

    public int getTpaRequestExpiry() {
        return getConfig().getInt("tpa.request-expiry", 15);
    }

    public ComponentMessage getTpaCannotRequestSelfMessage() {
        return getComponentMessage("messages.command.tpa.cannot-request-self", "<red>You cannot send a request to yourself!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getTpaTargetFlyingMessage() {
        return getComponentMessage("messages.command.tpa.target-flying", "<red>Cannot teleport because the target is flying!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getTpaToRequestSenderMessage(@NotNull Player target) {
        return getComponentMessage("messages.command.tpa.to.request-sender", "<color:#F0E68C>Requested to teleport to {target}")
            .replace(MessageConfig.getInstance().getPrefixReplacer())
            .replace("{target}", target.getName());
    }

    public ComponentMessage getTpaToRequestTargetMessage(@NotNull Player sender) {
        return getComponentMessage("messages.command.tpa.to.request-target", "<color:#F0E68C>{player} wants to teleport to you! {accept} {deny}")
            .replace(MessageConfig.getInstance().getPrefixReplacer())
            .replace("{player}", sender.getName());
    }

    public ComponentMessage getTpaHereRequestSenderMessage(@NotNull Player target) {
        return getComponentMessage("messages.command.tpa.here.request-sender", "<color:#F0E68C>Invited {target} to teleport to you")
            .replace(MessageConfig.getInstance().getPrefixReplacer())
            .replace("{target}", target.getName());
    }

    public ComponentMessage getTpaHereRequestTargetMessage(@NotNull Player sender) {
        return getComponentMessage("messages.command.tpa.here.request-target", "<color:#F0E68C>{player} wants you to teleport to them! {accept} {deny}")
            .replace(MessageConfig.getInstance().getPrefixReplacer())
            .replace("{player}", sender.getName());
    }

    public ComponentMessage getTpaRequestAcceptedTargetMessage() {
        return getComponentMessage("messages.command.tpa.accepted-target", "<color:#F0E68C>Teleport request accepted!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getTpaRequestAcceptedTeleporterMessage() {
        return getComponentMessage("messages.command.tpa.accepted-teleporter", "<color:#F0E68C>Teleport request accepted! Teleporting...").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getTpaRequestDeniedSenderMessage(@NotNull Player target) {
        return getComponentMessage("messages.command.tpa.request-denied-sender", "<color:#F0E68C>{target} has denied your teleport request!")
            .replace(MessageConfig.getInstance().getPrefixReplacer())
            .replace("{target}", target.getName());
    }

    public ComponentMessage getTpaRequestDeniedTargetMessage() {
        return getComponentMessage("messages.command.tpa.request-denied-target", "<color:#F0E68C>Denied the teleport request!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getTpaAcceptClickMessage(@NotNull Player sender) {
        String message = getConfig().getString("messages.command.tpa.accept-click", "<green><click:run_command:'/tpaccept {sender}'>[Accept]").replace("{sender}", sender.getName())
        return ComponentMessage.componentMessage(message);
    }

    public ComponentMessage getTpaDenyClickMessage(@NotNull Player sender) {
        String message = getConfig().getString("messages.command.tpa.deny-click", "<red><click:run_command:'/tpdeny {sender}'>[Deny]").replace("{sender}", sender.getName());
        return ComponentMessage.componentMessage(message);
    }

    public int getTPAWarmupSeconds() {
        return getConfig().getInt("tpa.warmup", 0);
    }

    // /back related things

    public boolean shouldBackSaveDeath() {
        return getConfig().getBoolean("back.save-death", true);
    }

    public ComponentMessage getBackTeleportedMessage() {
        return getComponentMessage("messages.command.back.teleported-back", "<color:#F0E68C>You have been teleported to your last saved location.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getBackTeleportedSenderMessage() {
        return getComponentMessage("messages.command.back.teleported-back-sender", "<color:#F0E68C>You have teleported {target} to their last saved location.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getDBackTeleportedMessage() {
        return getComponentMessage("messages.command.dback.teleported-back", "<color:#F0E68C>You have been teleported to your last death location.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getDBackTeleportedSenderMessage() {
        return getComponentMessage("messages.command.dback.teleported-back-sender", "<color:#F0E68C>You have teleported {target} to their last death location.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public int getBackWarmupSeconds() {
        return getConfig().getInt("back.warmup", 0);
    }

}
