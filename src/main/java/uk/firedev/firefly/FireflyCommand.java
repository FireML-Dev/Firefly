package uk.firedev.firefly;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.BooleanArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.LiteralArgument;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.ModuleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FireflyCommand {

    public static CommandTree getCommand() {
        return new CommandTree("firefly")
            .withPermission("firefly.command.main")
            .withShortDescription("Manage the Plugin")
            .then(getReloadBranch())
            .then(getModulesBranch());
    }

    private static Argument<String> getReloadBranch() {
        return new LiteralArgument("reload")
            .executes(info -> {
                Firefly.getInstance().reload();
                MessageConfig.getInstance().getMainCommandReloadedMessage().sendMessage(info.sender());
            });
    }

    private static Argument<String> getModulesBranch() {
        return new LiteralArgument("modules")
            .executes(info -> {
                getModulesMessage().sendMessage(info.sender());
            });
    }

    /**
     * Creates a message for the modules command.
     * This message is designed to look like /plugins
     */
    private static ComponentMessage getModulesMessage() {
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

        return ComponentMessage.of(finalComponent);
    }

}
