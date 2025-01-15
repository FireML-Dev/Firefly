package uk.firedev.firefly.modules.command.commands.flight;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.IntegerArgument;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.modules.command.CommandConfig;

import java.util.Objects;

public class FlySpeedCommand extends Command {

    private final CommandTree command = new CommandTree(getName())
            .withPermission(getPermission())
            .withShortDescription("Adjusts the speed of flight")
            .then(
                    new IntegerArgument("speed", 1, 10)
                            .executesPlayer((player, arguments) -> {
                                int speed = (int) Objects.requireNonNull(arguments.get("speed"));
                                setSpeed(player, player, speed);
                            })
                            .then(new EntitySelectorArgument.OnePlayer("target")
                                    .executes((sender, arguments) -> {
                                        Player player = (Player) arguments.get("target");
                                        if (player == null) {
                                            MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(sender);
                                            return;
                                        }
                                        int speed = (int) Objects.requireNonNull(arguments.get("speed"));
                                        setSpeed(sender, player, speed);
                                    }))
            );

    private void setSpeed(@NotNull CommandSender sender, @NotNull Player target, int speed) {
        target.setFlySpeed((float) speed / 10);
        sendSetMessage(sender, target, speed);
    }

    private void sendSetMessage(@NotNull CommandSender sender, @NotNull Player target, int speed) {
        CommandConfig.getInstance().getFlySpeedSetMessage()
                .replace("speed", String.valueOf(speed))
                .sendMessage(target);
        if (!target.equals(sender)) {
            CommandConfig.getInstance().getFlySpeedSetSenderMessage()
                    .replace("speed", String.valueOf(speed))
                    .replace("target", target.name())
                    .sendMessage(sender);
        }
    }

    @Override
    public String getName() {
        return "flyspeed";
    }

    @Override
    public CommandTree getCommand() {
        return command;
    }

}
