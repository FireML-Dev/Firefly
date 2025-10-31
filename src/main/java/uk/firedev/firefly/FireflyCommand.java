package uk.firedev.firefly;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.ModuleManager;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;

import java.util.ArrayList;
import java.util.List;

public class FireflyCommand {

    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("firefly")
            .requires(stack -> stack.getSender().hasPermission("firefly.command.main"))
            .then(reload())
            .then(modules())
            .build();
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reload() {
        return Commands.literal("reload")
            .executes(context -> {
                Firefly.getInstance().reload();
                MessageConfig.getInstance().getMainCommandReloadedMessage().send(context.getSource().getSender());
                return 1;
            });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> modules() {
        return Commands.literal("modules")
            .executes(context -> {
                getModulesMessage().send(context.getSource().getSender());
                return 1;
            });
    }

    /**
     * Creates a message for the modules command.
     * This message is designed to look like /plugins
     */
    private static ComponentSingleMessage getModulesMessage() {
        List<Module> modules = ModuleManager.getInstance().getModules();
        List<Component> formattedModules = new ArrayList<>();

        int loadedModules = modules.size();

        for (Module module : ModuleManager.getInstance().getModules()) {
            Component formatted = Component
                .text(module.getIdentifier())
                .color(module.isLoaded() ? NamedTextColor.GREEN : NamedTextColor.RED);
            formattedModules.add(formatted);
        }

        Component finalComponent = Component
            .text("Modules (" + loadedModules + "):")
            .appendNewline()
            .append(Component.join(JoinConfiguration.commas(true), formattedModules));

        return ComponentMessage.componentMessage(finalComponent);
    }

}
