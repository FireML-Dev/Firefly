package uk.firedev.firefly;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.ModuleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This command should never be unloaded.
 */
public class FireflyCommand extends CommandAPICommand {

    private static FireflyCommand instance = null;

    private FireflyCommand() {
        super("firefly");
        setPermission(CommandPermission.fromString("firefly.command.main"));
        withShortDescription("Manage the Plugin");
        withFullDescription("Manage the Plugin");
        withSubcommands(getReloadCommand(), getModulesCommand());
        executes((sender, arguments) -> {
            MessageConfig.getInstance().getMainCommandUsageMessage().sendMessage(sender);
        });
    }

    public static FireflyCommand getInstance() {
        if (instance == null) {
            instance = new FireflyCommand();
        }
        return instance;
    }

    private CommandAPICommand getReloadCommand() {
        return new CommandAPICommand("reload")
                .executes(((sender, arguments) -> {
                    Firefly.getInstance().reload();
                    MessageConfig.getInstance().getMainCommandReloadedMessage().sendMessage(sender);
                }));
    }

    private CommandAPICommand getModulesCommand() {
        return new CommandAPICommand("modules")
                .executes((sender, arguments) -> {
                    getModulesMessage().sendMessage(sender);
                });
    }

    /**
     * Creates a message for the modules command.
     * This message is designed to look like /plugins
     */
    private ComponentMessage getModulesMessage() {
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
