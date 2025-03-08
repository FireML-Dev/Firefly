package uk.firedev.firefly.modules.kit.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.ArgumentBuilder;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.CustomArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.firefly.modules.kit.Kit;
import uk.firedev.firefly.modules.kit.KitModule;

import java.util.function.BiPredicate;

public class KitArgument {

    public static Argument<Kit> create(@NotNull String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            Kit kit = KitModule.getInstance().getKit(info.input());
            if (kit == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(
                    new CustomArgument.MessageBuilder("Invalid kit: ").appendArgInput()
                );
            }
            return kit;
        }).includeSuggestions(
            ArgumentBuilder.getAsyncSuggestions(info ->
                KitModule.getInstance().getKits().values().stream().map(Kit::getName).toArray(String[]::new)
            )
        );
    }

    public static Argument<Kit> createPredicate(@NotNull String nodeName, @NotNull BiPredicate<Player, Kit> predicate) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            Kit kit = KitModule.getInstance().getKit(info.input());
            if (kit == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(
                    new CustomArgument.MessageBuilder("Invalid kit: ").appendArgInput()
                );
            }
            if (info.sender() instanceof Player player && !predicate.test(player, kit)) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(
                    new CustomArgument.MessageBuilder("Cannot use kit: ").appendArgInput()
                );
            }
            return kit;
        }).includeSuggestions(
            ArgumentBuilder.getAsyncSuggestions(info ->
                KitModule.getInstance().getKits().values().stream()
                    .filter(kit -> {
                        if (!(info.sender() instanceof Player player)) {
                            return true;
                        }
                        return predicate.test(player, kit);
                    })
                    .map(Kit::getName)
                    .toArray(String[]::new)
            )
        );
    }

}
