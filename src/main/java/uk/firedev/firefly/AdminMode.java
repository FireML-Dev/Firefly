package uk.firedev.firefly;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.firefly.config.MessageConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminMode {

    private static final List<UUID> adminModeUsers = new ArrayList<>();

    public static String PERMISSION = "firefly.admin";
    
    public static void toggleAdminMode(@NotNull Player admin) {
        if (isAdminMode(admin)) {
            disableAdminMode(admin);
        } else {
            enableAdminMode(admin);
        }
    }

    public static void enableAdminMode(@NotNull Player admin) {
        if (!admin.hasPermission(PERMISSION)) {
            MessageConfig.getInstance().getAdminModeNoPermissionMessage().sendMessage(admin);
            return;
        }
        if (adminModeUsers.contains(admin.getUniqueId())) {
            return;
        }
        adminModeUsers.add(admin.getUniqueId());
        MessageConfig.getInstance().getAdminModeEnabledMessage().sendMessage(admin);
    }

    public static void disableAdminMode(@NotNull Player admin) {
        adminModeUsers.remove(admin.getUniqueId());
        MessageConfig.getInstance().getAdminModeDisabledMessage().sendMessage(admin);
    }

    public static boolean isAdminMode(@NotNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            return sender.hasPermission(PERMISSION);
        }
        return player.hasPermission(PERMISSION) && adminModeUsers.contains(player.getUniqueId());
    }

}
