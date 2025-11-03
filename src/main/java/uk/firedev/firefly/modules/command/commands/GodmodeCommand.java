package uk.firedev.firefly.modules.command.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.placeholders.Placeholders;

public class GodmodeCommand implements Command, Listener {

    @NotNull
    @Override
    public String getConfigName() {
        return "godmode";
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
    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal(getCommandName())
            .requires(stack -> isConfigEnabled() &&  stack.getSender().hasPermission(getPermission()))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                toggleGodmode(player, player);
                return 1;
            })
            .then(
                Commands.argument("target", PlayerArgument.create())
                    .requires(stack -> stack.getSender().hasPermission(getTargetPermission()))
                    .executes(context -> {
                        Player player = context.getArgument("target", Player.class);
                        toggleGodmode(context.getSource().getSender(), player);
                        return 1;
                    })
            )
            .build();
    }

    @Override
    public void registerCommands(@NotNull Commands registrar) {
        registrar.register(get());
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent event) {
        if (!event.getEntity().isInvulnerable()) {
            return;
        }
        if (shouldPreventHunger()) {
            event.setCancelled(true);
        }
    }

    // Convenience

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

    // Config

    public boolean shouldPreventHunger() {
        return getConfig().getBoolean("prevent-hunger", true);
    }
    
    // Messages

    public ComponentMessage getEnabledMessage() {
        return getMessage("enabled", "{prefix}<color:#F0E68C>Godmode is now enabled.");
    }

    public ComponentMessage getEnabledSenderMessage() {
        return getMessage("enabled-sender", "{prefix}<color:#F0E68C>Godmode is now enabled for {target}.");
    }

    public ComponentMessage getDisabledMessage() {
        return getMessage("disabled", "{prefix}<red>Godmode is now disabled.");
    }

    public ComponentMessage getDisabledSenderMessage() {
        return getMessage("disabled-sender", "{prefix}<red>Godmode is now disabled for {target}.");
    }

}
