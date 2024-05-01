package uk.firedev.skylight;

import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.skylight.config.MessageConfig;

public class SkylightCommand extends CommandAPICommand {

    private static SkylightCommand instance = null;

    private SkylightCommand() {
        super("skylight");
        setPermission(CommandPermission.fromString("skylight.command.main"));
        withShortDescription("Manage the Plugin");
        withFullDescription("Manage the Plugin");
        withSubcommands(getReloadCommand());
        executes((sender, arguments) -> {
            MessageConfig.getInstance().getMainCommandUsageMessage().sendMessage(sender);
        });
    }

    public static SkylightCommand getInstance() {
        if (instance == null) {
            instance = new SkylightCommand();
        }
        return instance;
    }

    private CommandAPICommand getReloadCommand() {
        return new CommandAPICommand("reload")
                .executes(((sender, arguments) -> {
                    Skylight.getInstance().reload();
                    MessageConfig.getInstance().getMainCommandReloadedMessage().sendMessage(sender);
                }));
    }

}
