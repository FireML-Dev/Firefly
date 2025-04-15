package uk.firedev.firefly.modules.command.commands;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.modules.command.CommandConfig;

import java.util.Objects;

public class HealCommand extends Command {

    @NotNull
    @Override
    public String getConfigName() {
        return "heal";
    }

    private void heal(@NotNull CommandSender sender, @NotNull Player target) {
        double maxHealth = 20;
        AttributeInstance attribute = target.getAttribute(Attribute.MAX_HEALTH);
        if (attribute != null) {
            maxHealth = attribute.getValue();
        }
        target.heal(maxHealth);
        target.setFoodLevel(20);
        sendHealedMessage(sender, target);
    }

    private void sendHealedMessage(@NotNull CommandSender sender, @NotNull Player target) {
        CommandConfig.getInstance().getHealedMessage().sendMessage(target);
        if (!target.equals(sender)) {
            CommandConfig.getInstance().getHealedSenderMessage()
                .replace("target", target.name())
                .sendMessage(sender);
        }
    }

    @NotNull
    @Override
    public CommandTree loadCommand() {
        return new CommandTree(getName())
            .withAliases(getAliases())
            .withPermission(getPermission())
            .executesPlayer(info -> {
                if (disabledCheck(info.sender())) {
                    return;
                }
                heal(info.sender(), info.sender());
            })
            .then(
                new EntitySelectorArgument.OnePlayer("target")
                    .withPermission(getTargetPermission())
                    .executes(info -> {
                        if (disabledCheck(info.sender())) {
                            return;
                        }
                        Player player = Objects.requireNonNull(info.args().getUnchecked("target"));
                        heal(info.sender(), player);
                    })
            );
    }

}
