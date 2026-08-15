package uk.firedev.firefly.modules.economy.baltop;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class BaltopEntry {

    private final @NonNull UUID uuid;
    private final double balance;
    private final OfflinePlayer player;

    public BaltopEntry(@NonNull UUID uuid, double balance) {
        this.uuid = uuid;
        this.balance = balance;
        this.player = Bukkit.getOfflinePlayer(uuid);
    }

    public @NonNull UUID uuid() {
        return this.uuid;
    }

    public double balance() {
        return this.balance;
    }

    public @NonNull OfflinePlayer player() {
        return this.player;
    }

}
