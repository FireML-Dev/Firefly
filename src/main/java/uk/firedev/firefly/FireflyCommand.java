package uk.firedev.firefly;

import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.ModuleManager;

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
        StringBuilder message = new StringBuilder();
        Map<String, Module> loadedModules = ModuleManager.getInstance().getLoadedModules();
        message.append("Modules (").append(loadedModules.size()).append("):").append("\n");
        loadedModules.forEach((uppercaseIdentifier, module) -> {
            String color = module.isLoaded() ? "<green>" : "<red>";
            message.append(color).append(module.getIdentifier()).append("<white>, ");
        });
        return ComponentMessage.fromString(message.toString());
    }

}
