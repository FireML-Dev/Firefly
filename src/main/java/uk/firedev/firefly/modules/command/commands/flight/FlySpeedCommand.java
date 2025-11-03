package uk.firedev.firefly.modules.command.commands.flight;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.firefly.modules.command.Command;

public class FlySpeedCommand implements Command {

    @NotNull
    @Override
    public String getConfigName() {
        return "flyspeed";
    }

    @NotNull
    @Override
    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal(getCommandName())
            .requires(stack -> isConfigEnabled() && stack.getSender().hasPermission(getPermission()))
            .then(
                Commands.argument("speed", FloatArgumentType.floatArg(1, 10))
                    .executes(context -> {
                        Player player = CommandUtils.requirePlayer(context.getSource());
                        if (player == null) {
                            return 1;
                        }
                        float speed = context.getArgument("speed", float.class);
                        setSpeed(player, player, speed);
                        return 1;
                    })
                    .then(
                        Commands.argument("target", PlayerArgument.create())
                            .requires(stack -> stack.getSender().hasPermission(getTargetPermission()))
                            .executes(context -> {
                                Player player = context.getArgument("target", Player.class);
                                float speed = context.getArgument("speed", float.class);
                                setSpeed(context.getSource().getSender(), player, speed);
                                return 1;
                            })
                    )
            )
            .build();
    }

    @Override
    public void registerCommands(@NotNull Commands registrar) {
        registrar.register(get());
    }

    // Convenience

    private void setSpeed(@NotNull CommandSender sender, @NotNull Player target, float speed) {
        target.setFlySpeed(speed / 10F);
        sendSetMessage(sender, target, speed);
    }

    private void sendSetMessage(@NotNull CommandSender sender, @NotNull Player target, float speed) {
        getSetMessage()
            .replace("{speed}", String.valueOf(speed))
            .send(target);
        if (!target.equals(sender)) {
            getSetSenderMessage()
                .replace("{speed}", String.valueOf(speed))
                .replace("{target}", target.name())
                .send(sender);
        }
    }
    
    // Messages

    public ComponentMessage getSetMessage() {
        return getMessage("set", "{prefix}<color:#F0E68C>Your fly speed has been set to {speed}.");
    }

    public ComponentMessage getSetSenderMessage() {
        return getMessage("set-sender", "{prefix}<color:#F0E68C>{target}'s fly speed has been set to {speed}.");
    }

}
