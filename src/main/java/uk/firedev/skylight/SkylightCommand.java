package uk.firedev.skylight;

import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.skylight.config.MessageConfig;
import uk.firedev.skylight.modules.elevator.ElevatorManager;
import uk.firedev.skylight.modules.kit.KitManager;
import uk.firedev.skylight.modules.nickname.NicknameManager;
import uk.firedev.skylight.modules.small.AmethystProtection;
import uk.firedev.skylight.modules.small.LootChestProtection;
import uk.firedev.skylight.modules.titles.TitleManager;

/**
 * This command should never be unloaded.
 */
public class SkylightCommand extends CommandAPICommand {

    private static SkylightCommand instance = null;

    private SkylightCommand() {
        super("skylight");
        setPermission(CommandPermission.fromString("skylight.command.main"));
        withShortDescription("Manage the Plugin");
        withFullDescription("Manage the Plugin");
        withSubcommands(getReloadCommand(), getModulesCommand());
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

    private CommandAPICommand getModulesCommand() {
        return new CommandAPICommand("modules")
                .executes((sender, arguments) -> {
                    MessageConfig.getInstance().getMainCommandModulesMessage().applyReplacer(getModulesReplacer()).sendMessage(sender);
                });
    }

    private ComponentReplacer getModulesReplacer() {
        return new ComponentReplacer().addReplacements(
                "kitsEnabled", KitManager.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled",
                "apEnabled", AmethystProtection.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled",
                "lcpEnabled", LootChestProtection.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled",
                "elevatorsEnabled", ElevatorManager.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled",
                "titlesEnabled", TitleManager.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled",
                "nicknamesEnabled", NicknameManager.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled"
        );
    }

}
