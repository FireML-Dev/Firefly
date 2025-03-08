package uk.firedev.firefly.modules.command.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.modules.command.CommandConfig;
import uk.firedev.firefly.placeholders.Placeholders;

import java.util.Objects;

public class GodmodeCommand extends Command {

    @NotNull
    @Override
    public String getConfigName() {
        return "godmode";
    }

    private void toggleGodmode(@NotNull CommandSender sender, @NotNull Player target) {
        if (target.isInvulnerable()) {
            target.setInvulnerable(false);
            sendDisabledMessage(sender, target);
        } else {
            target.setInvulnerable(true);
            sendEnabledMessage(sender, target);
        }
    }

    private void sendEnabledMessage(@NotNull CommandSender sender, @NotNull Player target) {
        CommandConfig.getInstance().getGodmodeEnabledMessage().sendMessage(target);
        if (!target.equals(sender)) {
            CommandConfig.getInstance().getGodmodeEnabledSenderMessage()
                    .replace("target", target.name())
                    .sendMessage(sender);
        }
    }

    private void sendDisabledMessage(@NotNull CommandSender sender, @NotNull Player target) {
        CommandConfig.getInstance().getGodmodeDisabledMessage().sendMessage(target);
        if (!target.equals(sender)) {
            CommandConfig.getInstance().getGodmodeDisabledSenderMessage()
                    .replace("target", target.name())
                    .sendMessage(sender);
        }
    }

    @Override
    public void registerPlaceholders() {
        Placeholders.manageProvider(provider ->
            provider.addAudiencePlaceholder("is_godmode", audience -> {
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                return Component.text(player.isInvulnerable());
            }));
    }

    @NotNull
    @Override
    public CommandTree refreshCommand() {
        return new CommandTree(getName())
                .withAliases(getAliases())
                .withPermission(getPermission())
                .executesPlayer(info -> {
                    toggleGodmode(info.sender(), info.sender());
                })
                .then(
                        new EntitySelectorArgument.OnePlayer("target")
                                .withPermission(getTargetPermission())
                                .executes((sender, arguments) -> {
                                    Player player = (Player) Objects.requireNonNull(arguments.get("target"));
                                    toggleGodmode(sender, player);
                                })
                );
    }

}
