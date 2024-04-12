package uk.firedev.skylight.placeholders;

import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.daisylib.utils.ComponentUtils;
import uk.firedev.skylight.modules.titles.TitleManager;
import uk.firedev.skylight.modules.small.AmethystProtection;

public class MiniPlaceholdersExpansion {

    public void register() {
        Expansion.builder("skylight")
                .filter(Player.class)
                .audiencePlaceholder("player_prefix", ((audience, argumentQueue, context) -> {
                    Player player = (Player) audience;
                    if (TitleManager.getInstance().isLoaded()) {
                        return Tag.selfClosingInserting(TitleManager.getInstance().getPlayerPrefix(player));
                    } else {
                        return Tag.selfClosingInserting(ComponentUtils.parseComponent(VaultManager.getChat().getPlayerPrefix(player)));
                    }
                }))
                .audiencePlaceholder("player_suffix", ((audience, argumentQueue, context) -> {
                    Player player = (Player) audience;
                    if (TitleManager.getInstance().isLoaded()) {
                        return Tag.selfClosingInserting(TitleManager.getInstance().getPlayerSuffix(player));
                    } else {
                        return Tag.selfClosingInserting(ComponentUtils.parseComponent(VaultManager.getChat().getPlayerSuffix(player)));
                    }
                }))
                .audiencePlaceholder("amethyst_protected", ((audience, argumentQueue, context) -> {
                    if (AmethystProtection.getInstance().isLoaded()) {
                        return Tag.selfClosingInserting(Component.text(!AmethystProtection.getInstance().isDisabled((Player) audience)));
                    } else {
                        return Tag.selfClosingInserting(Component.text(false));
                    }
                }))
                .build()
                .register();
    }

}
