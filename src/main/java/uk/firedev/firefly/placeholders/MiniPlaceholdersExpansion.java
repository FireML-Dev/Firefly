package uk.firedev.firefly.placeholders;

import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.modules.small.AmethystProtection;
import uk.firedev.firefly.modules.titles.TitleModule;

import java.util.Objects;

public class MiniPlaceholdersExpansion {

    public void register() {
        Expansion.builder("firefly")
                .filter(Player.class)
                .audiencePlaceholder("player_prefix", ((audience, argumentQueue, context) -> {
                    Player player = (Player) audience;
                    if (TitleModule.getInstance().isLoaded()) {
                        return Tag.selfClosingInserting(TitleModule.getInstance().getPlayerPrefix(player));
                    } else {
                        String prefix = Objects.requireNonNull(VaultManager.getChat()).getPlayerPrefix(player);
                        return Tag.selfClosingInserting(ComponentMessage.fromString(prefix).getMessage());
                    }
                }))
                .audiencePlaceholder("player_suffix", ((audience, argumentQueue, context) -> {
                    Player player = (Player) audience;
                    if (TitleModule.getInstance().isLoaded()) {
                        return Tag.selfClosingInserting(TitleModule.getInstance().getPlayerSuffix(player));
                    } else {
                        String suffix = Objects.requireNonNull(VaultManager.getChat()).getPlayerSuffix(player);
                        return Tag.selfClosingInserting(ComponentMessage.fromString(suffix).getMessage());
                    }
                }))
                .audiencePlaceholder("player_nickname", ((audience, argumentQueue, context) -> {
                    Player player = (Player) audience;
                    if (NicknameModule.getInstance().isLoaded()) {
                        return Tag.selfClosingInserting(NicknameModule.getInstance().getNickname(player));
                    } else {
                        return Tag.selfClosingInserting(ComponentMessage.fromString(player.getName()).getMessage());
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
