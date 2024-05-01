package uk.firedev.skylight.placeholders;

import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.skylight.modules.nickname.NicknameManager;
import uk.firedev.skylight.modules.small.AmethystProtection;
import uk.firedev.skylight.modules.titles.TitleManager;

import java.util.Objects;

public class MiniPlaceholdersExpansion {

    public void register() {
        Expansion.builder("skylight")
                .filter(Player.class)
                .audiencePlaceholder("player_prefix", ((audience, argumentQueue, context) -> {
                    Player player = (Player) audience;
                    if (TitleManager.getInstance().isLoaded()) {
                        return Tag.selfClosingInserting(TitleManager.getInstance().getPlayerPrefix(player));
                    } else {
                        String prefix = Objects.requireNonNull(VaultManager.getChat()).getPlayerPrefix(player).replace('§', '&');
                        return Tag.selfClosingInserting(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix));
                    }
                }))
                .audiencePlaceholder("player_suffix", ((audience, argumentQueue, context) -> {
                    Player player = (Player) audience;
                    if (TitleManager.getInstance().isLoaded()) {
                        return Tag.selfClosingInserting(TitleManager.getInstance().getPlayerSuffix(player));
                    } else {
                        String suffix = Objects.requireNonNull(VaultManager.getChat()).getPlayerSuffix(player).replace('§', '&');
                        return Tag.selfClosingInserting(LegacyComponentSerializer.legacyAmpersand().deserialize(suffix));
                    }
                }))
                .audiencePlaceholder("player_nickname", ((audience, argumentQueue, context) -> {
                    Player player = (Player) audience;
                    if (NicknameManager.getInstance().isLoaded()) {
                        return Tag.selfClosingInserting(NicknameManager.getInstance().getNickname(player));
                    } else {
                        return Tag.selfClosingInserting(new ComponentMessage(player.getName()).getMessage());
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
