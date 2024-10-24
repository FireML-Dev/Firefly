package uk.firedev.firefly.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.firefly.modules.nickname.NicknameManager;
import uk.firedev.firefly.modules.small.AmethystProtection;
import uk.firedev.firefly.modules.titles.TitleManager;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

    @Override
    public boolean persist() { return true; }

    @Override
    public @NotNull String getIdentifier() { return "firefly"; }

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
            case "player_prefix" -> {
                if (TitleManager.getInstance().isLoaded()) {
                    yield TitleManager.getInstance().getPlayerPrefixLegacy(player);
                } else {
                    yield null;
                }
            }
            case "player_suffix" -> {
                if (TitleManager.getInstance().isLoaded()) {
                    yield TitleManager.getInstance().getPlayerSuffixLegacy(player);
                } else {
                    yield null;
                }
            }
            case "amethyst_protected" -> {
                if (AmethystProtection.getInstance().isLoaded()) {
                    yield String.valueOf(!AmethystProtection.getInstance().isDisabled(player));
                } else {
                    yield null;
                }
            }
            case "player_nickname" -> {
                if (NicknameManager.getInstance().isLoaded()) {
                    yield NicknameManager.getInstance().getStringNickname(player);
                }
                yield player.getName();
            }
            default -> null;
        };
    }

}
