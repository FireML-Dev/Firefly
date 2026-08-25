package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.config.BasicConfig;
import uk.firedev.daisylib.messages.message.ComponentMessage;
import uk.firedev.daisylib.utils.CommonUtils;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

public class TeleportConfig extends BasicConfig {

    private static TeleportConfig instance;

    private TeleportConfig() {
        super("modules/teleport.yml", "modules/teleport.yml", Firefly.getInstance());
    }

    public static TeleportConfig getInstance() {
        if (instance == null) {
            instance = new TeleportConfig();
        }
        return instance;
    }

    // General messages

    public ComponentMessage<?, ?>  getTeleportedMessage() {
        return getComponentMessage("messages.teleported", "<color:#F0E68C>You have teleported to {target-location}!");
    }

    public ComponentMessage<?, ?>  getLocationInvalidMessage() {
        return getComponentMessage("messages.location-invalid", "<red>That location is not valid!");
    }

    // Spawn command related things

    public void setSpawnLocation(boolean firstSpawn, @NonNull Location location) {
        String spawnLocKey = firstSpawn ? "spawn.first-spawn-location" : "spawn.spawn-location";
        CommonUtils.addLocationToConfig(getConfig(), spawnLocKey, location);
        save();
        TeleportModule.getInstance().refreshSpawnLocations();
    }

    public Location getSpawnLocation(boolean firstSpawn) {
        String spawnLocKey = firstSpawn ? "spawn.first-spawn-location" : "spawn.spawn-location";
        String invalidLocation = firstSpawn ? "The first spawn location is not valid!" : "The spawn location is not valid!";

        Location location = CommonUtils.getLocationFromConfig(getConfig(), spawnLocKey);
        if (location == null) {
            Firefly.getInstance().getLogging().warn(invalidLocation);
            return null;
        }
        return location;
    }

    public boolean isSpawnOnJoin() {
        return getConfig().getBoolean("spawn.spawn-on-join");
    }

    public ComponentMessage<?, ?>  getSpawnSentPlayerToSpawnMessage(@NonNull Player targetPlayer) {
        return getComponentMessage("messages.command.spawn.sent-player-to-spawn", "<color:#F0E68C>Sent {target} to spawn.")
            .replace("{target}", targetPlayer.getName());
    }

    public ComponentMessage<?, ?>  getSpawnTeleportedToSpawnMessage() {
        return getComponentMessage("messages.command.spawn.teleported-to-spawn", "<color:#F0E68C>You have been teleported to spawn.");
    }

    public ComponentMessage<?, ?>  getSpawnSetSpawnMessage() {
        return getComponentMessage("messages.command.spawn.spawn-set", "<color:#F0E68C>Set the spawn location to your current location.");
    }

    public ComponentMessage<?, ?>  getSpawnSetFirstSpawnMessage() {
        return getComponentMessage("messages.command.spawn.first-spawn-set", "<color:#F0E68C>Set the first spawn location to your current location.");
    }

    public int getSpawnWarmupSeconds() {
        return getConfig().getInt("spawn.warmup", 0);
    }

    // TPA related things

    public int getTpaRequestExpiry() {
        return getConfig().getInt("tpa.request-expiry", 15);
    }

    public ComponentMessage<?, ?>  getTpaCannotRequestSelfMessage() {
        return getComponentMessage("messages.command.tpa.cannot-request-self", "<red>You cannot send a request to yourself!");
    }

    public ComponentMessage<?, ?>  getTpaTargetFlyingMessage() {
        return getComponentMessage("messages.command.tpa.target-flying", "<red>Cannot teleport because the target is flying!");
    }

    public ComponentMessage<?, ?>  getTpaToRequestSenderMessage(@NonNull Player target) {
        return getComponentMessage("messages.command.tpa.to.request-sender", "<color:#F0E68C>Requested to teleport to {target}")
            .replace("{target}", target.getName());
    }

    public ComponentMessage<?, ?>  getTpaToRequestTargetMessage(@NonNull Player sender) {
        return getComponentMessage("messages.command.tpa.to.request-target", "<color:#F0E68C>{player} wants to teleport to you! {accept} {deny}")
            .replace("{player}", sender.getName());
    }

    public ComponentMessage<?, ?>  getTpaHereRequestSenderMessage(@NonNull Player target) {
        return getComponentMessage("messages.command.tpa.here.request-sender", "<color:#F0E68C>Invited {target} to teleport to you")
            .replace("{target}", target.getName());
    }

    public ComponentMessage<?, ?>  getTpaHereRequestTargetMessage(@NonNull Player sender) {
        return getComponentMessage("messages.command.tpa.here.request-target", "<color:#F0E68C>{player} wants you to teleport to them! {accept} {deny}")
            .replace("{player}", sender.getName());
    }

    public ComponentMessage<?, ?>  getTpaRequestAcceptedTargetMessage() {
        return getComponentMessage("messages.command.tpa.accepted-target", "<color:#F0E68C>Teleport request accepted!");
    }

    public ComponentMessage<?, ?>  getTpaRequestAcceptedTeleporterMessage() {
        return getComponentMessage("messages.command.tpa.accepted-teleporter", "<color:#F0E68C>Teleport request accepted! Teleporting...");
    }

    public ComponentMessage<?, ?>  getTpaRequestDeniedSenderMessage(@NonNull Player target) {
        return getComponentMessage("messages.command.tpa.request-denied-sender", "<color:#F0E68C>{target} has denied your teleport request!")
            .replace("{target}", target.getName());
    }

    public ComponentMessage<?, ?>  getTpaRequestDeniedTargetMessage() {
        return getComponentMessage("messages.command.tpa.request-denied-target", "<color:#F0E68C>Denied the teleport request!");
    }

    public ComponentMessage<?, ?>  getTpaAcceptClickMessage(@NonNull Player sender) {
        String message = getConfig().getString("messages.command.tpa.accept-click", "<green><click:run_command:'/tpaccept {sender}'>[Accept]").replace("{sender}", sender.getName());
        return ComponentMessage.componentMessage(message);
    }

    public ComponentMessage<?, ?>  getTpaDenyClickMessage(@NonNull Player sender) {
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

    public ComponentMessage<?, ?>  getBackTeleportedMessage() {
        return getComponentMessage("messages.command.back.teleported-back", "<color:#F0E68C>You have been teleported to your last saved location.");
    }

    public ComponentMessage<?, ?>  getBackTeleportedSenderMessage() {
        return getComponentMessage("messages.command.back.teleported-back-sender", "<color:#F0E68C>You have teleported {target} to their last saved location.");
    }

    public ComponentMessage<?, ?>  getDBackTeleportedMessage() {
        return getComponentMessage("messages.command.dback.teleported-back", "<color:#F0E68C>You have been teleported to your last death location.");
    }

    public ComponentMessage<?, ?>  getDBackTeleportedSenderMessage() {
        return getComponentMessage("messages.command.dback.teleported-back-sender", "<color:#F0E68C>You have teleported {target} to their last death location.");
    }

    public int getBackWarmupSeconds() {
        return getConfig().getInt("back.warmup", 0);
    }

    @Override
    public ComponentMessage<?, ?> getComponentMessage(@NonNull String path, @NonNull Object def) {
        return super.getComponentMessage(path, def).replace("{prefix}", MessageConfig.getInstance().getPrefix());
    }

}
