package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Config;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.message.component.ComponentMessage;
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
        getConfig().set(spawnLocKey + ".world", location.getWorld().getName());
        getConfig().set(spawnLocKey + ".x", location.getX());
        getConfig().set(spawnLocKey + ".y", location.getY());
        getConfig().set(spawnLocKey + ".z", location.getZ());
        getConfig().set(spawnLocKey + ".yaw", location.getYaw());
        getConfig().set(spawnLocKey + ".pitch", location.getPitch());
        try {
            getConfig().save();
            TeleportManager.getInstance().refreshSpawnLocations();
        } catch (IOException exception) {
            Loggers.warn(Firefly.getInstance().getComponentLogger(), "Failed to save spawn location to config!", exception);
        }
    }

    public Location getSpawnLocation(boolean firstSpawn) {
        String spawnLocKey = firstSpawn ? "spawn.first-spawn-location" : "spawn.spawn-location";
        String worldName = getConfig().getString(spawnLocKey + ".world");
        String missingWorld = firstSpawn ? "The world linked to the first spawn location is not valid!" : "The world linked to the spawn location is not valid!";
        if (worldName == null) {

            Loggers.warn(Firefly.getInstance().getComponentLogger(), missingWorld);
            return null;
        }
        double x = getConfig().getDouble(spawnLocKey + ".x");
        double y = getConfig().getDouble(spawnLocKey + ".y");
        double z = getConfig().getDouble(spawnLocKey + ".z");
        float yaw = getConfig().getFloat(spawnLocKey + ".yaw");
        float pitch = getConfig().getFloat(spawnLocKey + ".pitch");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Loggers.warn(Firefly.getInstance().getComponentLogger(), missingWorld);
            return null;
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    public boolean isSpawnOnJoin() {
        return getConfig().getBoolean("spawn.spawn-on-join");
    }

    // TPA related things

    public int getTpaMaximumCachedRequests() {
        return getConfig().getInt("tpa.cached-requests", 3);
    }

    public int getTpaRequestExpiry() {
        return getConfig().getInt("tpa.request-expiry", 15);
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
