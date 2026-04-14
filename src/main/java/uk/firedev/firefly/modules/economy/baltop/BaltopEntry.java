package uk.firedev.firefly.modules.economy.baltop;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.util.PlayerHelper;

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

    /**
     * Gets the OfflinePlayer linked to this entry.
     * <p>
     * You must ensure {@link #valid()} is true before calling this.
     */
    public @NonNull OfflinePlayer player() {
        if (!valid()) {
            throw new UnsupportedOperationException("Linked player is not valid.");
        }
        return this.player;
    }

    public boolean valid() {
        return PlayerHelper.hasPlayerBeenOnServer(player);
    }

}
