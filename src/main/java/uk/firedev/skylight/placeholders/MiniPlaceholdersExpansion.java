package uk.firedev.skylight.placeholders;

import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.local.VaultManager;
import uk.firedev.daisylib.utils.ComponentUtils;
import uk.firedev.skylight.chat.titles.TitleManager;
import uk.firedev.skylight.small.AmethystProtection;

import javax.swing.plaf.ComponentUI;

public class MiniPlaceholdersExpansion {

    public void register() {
        Expansion.builder("skylight")
                .filter(Player.class)
                .audiencePlaceholder("player_prefix", ((audience, argumentQueue, context) -> {
                    if (TitleManager.getInstance().isLoaded()) {
                        return Tag.selfClosingInserting(TitleManager.getInstance().getPlayerPrefix((Player) audience));
                    } else {
                        return notApplicable();
                    }
                }))
                .audiencePlaceholder("player_suffix", ((audience, argumentQueue, context) -> {
                    Player player = (Player) audience;
                    if (TitleManager.getInstance().isLoaded()) {
                        return Tag.selfClosingInserting(TitleManager.getInstance().getPlayerSuffix(player);
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
