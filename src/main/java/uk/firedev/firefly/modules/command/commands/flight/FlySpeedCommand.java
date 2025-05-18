package uk.firedev.firefly.modules.command.commands.flight;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.DoubleArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.FloatArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.IntegerArgument;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.modules.command.CommandConfig;

import java.util.Objects;

public class FlySpeedCommand extends Command {

    private void setSpeed(@NotNull CommandSender sender, @NotNull Player target, float speed) {
        target.setFlySpeed(speed / 10F);
        sendSetMessage(sender, target, speed);
    }

    private void sendSetMessage(@NotNull CommandSender sender, @NotNull Player target, float speed) {
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

    @NotNull
    @Override
    public String getConfigName() {
        return "flyspeed";
    }

    @NotNull
    @Override
    public CommandTree loadCommand() {
        return new CommandTree(getName())
            .withPermission(getPermission())
            .withAliases(getAliases())
            .withShortDescription("Adjusts the speed of flight")
            .then(
                new FloatArgument("speed", 1, 10)
                    .executesPlayer(info -> {
                        if (disabledCheck(info.sender())) {
                            return;
                        }
                        float speed = Objects.requireNonNull(info.args().getUnchecked("speed"));
                        setSpeed(info.sender(), info.sender(), speed);
                    })
                    .then(new EntitySelectorArgument.OnePlayer("target")
                        .withPermission(getTargetPermission())
                        .executes(info -> {
                            if (disabledCheck(info.sender())) {
                                return;
                            }
                            Player player = (Player) info.args().get("target");
                            if (player == null) {
                                MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(info.sender());
                                return;
                            }
                            float speed = Objects.requireNonNull(info.args().getUnchecked("speed"));
                            setSpeed(info.sender(), player, speed);
                        }))
            );
    }

}
