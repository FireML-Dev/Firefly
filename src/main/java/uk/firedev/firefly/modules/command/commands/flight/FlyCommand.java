package uk.firedev.firefly.modules.command.commands.flight;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.placeholders.Placeholders;

public class FlyCommand implements Command {

    @NotNull
    @Override
    public String getConfigName() {
        return "fly";
    }

    @Override
    public void registerPlaceholders() {
        Placeholders.manageProvider(provider ->
            provider.addAudiencePlaceholder("can_fly", audience -> {
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                return Component.text(player.getAllowFlight());
            }));
    }

    @NotNull
    @Override
    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal(getCommandName())
            .requires(stack -> stack.getSender().hasPermission(getPermission()))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                toggleFlight(player, player);
                return 1;
            })
            .then(
                Commands.argument("target", PlayerArgument.create())
                    .executes(context -> {
                        Player player = context.getArgument("target", Player.class);
                        toggleFlight(context.getSource().getSender(), player);
                        return 1;
                    })
            )
            .build();
    }

    // Convenience

    private void toggleFlight(@NotNull CommandSender sender, @NotNull Player target) {
        if (target.getAllowFlight()) {
            target.setFlying(false);
            target.setAllowFlight(false);
            sendDisabledMessage(sender, target);
        } else {
            target.setAllowFlight(true);
            sendEnabledMessage(sender, target);
        }
    }

    private void sendEnabledMessage(@NotNull CommandSender sender, @NotNull Player target) {
        getEnabledMessage().send(target);
        if (!target.equals(sender)) {
            getEnabledSenderMessage()
                .replace("{target}", target.name())
                .send(sender);
        }
    }

    private void sendDisabledMessage(@NotNull CommandSender sender, @NotNull Player target) {
        getDisabledMessage().send(target);
        if (!target.equals(sender)) {
            getDisabledSenderMessage()
                .replace("{target}", target.name())
                .send(sender);
        }
    }
    
    // Messages

    public ComponentMessage getEnabledMessage() {
        return getMessage("enabled", "{prefix}<color:#F0E68C>Flight is now enabled.");
    }

    public ComponentMessage getEnabledSenderMessage() {
        return getMessage("enabled-sender", "{prefix}<color:#F0E68C>Flight is now enabled for {target}.");
    }

    public ComponentMessage getDisabledMessage() {
        return getMessage("disabled", "{prefix}<red>Flight is now disabled.");
    }

    public ComponentMessage getDisabledSenderMessage() {
        return getMessage("disabled-sender", "{prefix}<red>Flight is now disabled for {target}.");
    }

}
