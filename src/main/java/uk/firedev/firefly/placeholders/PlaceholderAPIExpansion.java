package uk.firedev.firefly.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.modules.small.AmethystProtection;
import uk.firedev.firefly.modules.titles.TitleModule;

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
                if (TitleModule.getInstance().isLoaded()) {
                    yield TitleModule.getInstance().getPlayerPrefixLegacy(player);
                } else {
                    yield null;
                }
            }
            case "player_suffix" -> {
                if (TitleModule.getInstance().isLoaded()) {
                    yield TitleModule.getInstance().getPlayerSuffixLegacy(player);
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
                if (NicknameModule.getInstance().isLoaded()) {
                    yield NicknameModule.getInstance().getStringNickname(player);
                }
                yield player.getName();
            }
            default -> null;
        };
    }

}
