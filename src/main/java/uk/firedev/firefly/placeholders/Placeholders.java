package uk.firedev.firefly.placeholders;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.api.placeholders.PlaceholderProvider;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.modules.command.AmethystProtection;
import uk.firedev.firefly.modules.titles.TitleModule;

import java.util.Objects;

/**
 * Registers the plugin's placeholders using DaisyLib's PlaceholderProvider class.
 * <p>
 * This registers placeholders for both MiniPlaceholders and PlaceholderAPI.
 */
public class Placeholders {

    public static void register() {
        PlaceholderProvider.create(Firefly.getInstance())
                .addAudiencePlaceholder("player_prefix", audience -> {
                    if (!(audience instanceof Player player)) {
                        return Component.text("Player is not available.");
                    }
                    if (TitleModule.getInstance().isLoaded()) {
                        return TitleModule.getInstance().getPlayerPrefix(player);
                    } else {
                        String prefix = Objects.requireNonNull(VaultManager.getChat()).getPlayerPrefix(player);
                        return ComponentMessage.fromString(prefix).getMessage();
                    }
                })
                .addAudiencePlaceholder("player_suffix", audience -> {
                    if (!(audience instanceof Player player)) {
                        return Component.text("Player is not available.");
                    }
                    if (TitleModule.getInstance().isLoaded()) {
                        return TitleModule.getInstance().getPlayerSuffix(player);
                    } else {
                        String prefix = Objects.requireNonNull(VaultManager.getChat()).getPlayerSuffix(player);
                        return ComponentMessage.fromString(prefix).getMessage();
                    }
                })
                .addAudiencePlaceholder("player_nickname", audience -> {
                    if (!(audience instanceof Player player)) {
                        return Component.text("Player is not available.");
                    }
                    if (NicknameModule.getInstance().isLoaded()) {
                        return NicknameModule.getInstance().getNickname(player);
                    } else {
                        return ComponentMessage.fromString(player.getName()).getMessage();
                    }
                })
                .addAudiencePlaceholder("amethyst_protected", audience -> {
                    if (!(audience instanceof Player player)) {
                        return Component.text("Player is not available.");
                    }
                    if (AmethystProtection.getInstance().isLoaded()) {
                        return Component.text(!AmethystProtection.getInstance().isDisabled(player));
                    } else {
                        return Component.text(false);
                    }
                })
                .register();
    }

}
