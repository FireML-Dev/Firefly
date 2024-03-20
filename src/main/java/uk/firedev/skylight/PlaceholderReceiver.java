package uk.firedev.skylight;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.skylight.chat.titles.TitleManager;
import uk.firedev.skylight.small.AmethystProtection;

public class PlaceholderReceiver extends PlaceholderExpansion {

    @Override
    public boolean persist() { return true; }

    @Override
    public @NotNull String getIdentifier() { return "skylight"; }

    @Override
    public @NotNull String getAuthor() { return "FireML"; }

    @Override
    public @NotNull String getVersion() { return "1.0-SNAPSHOT"; }

    @Override
    public boolean canRegister() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return null;
        }
        return switch (identifier) {
            case "player_prefix" -> TitleManager.getInstance().getPlayerPrefixLegacy(player);
            case "player_suffix" -> TitleManager.getInstance().getPlayerSuffixLegacy(player);
            case "amethyst_protected" -> String.valueOf(!AmethystProtection.getInstance().isDisabled(player));
            default -> null;
        };
    }

}
