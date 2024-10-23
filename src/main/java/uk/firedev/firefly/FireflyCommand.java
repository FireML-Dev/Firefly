package uk.firedev.firefly;

import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.customcommands.CustomCommandsManager;
import uk.firedev.firefly.modules.elevator.ElevatorManager;
import uk.firedev.firefly.modules.kit.KitManager;
import uk.firedev.firefly.modules.nickname.NicknameManager;
import uk.firedev.firefly.modules.playtime.PlaytimeManager;
import uk.firedev.firefly.modules.small.AmethystProtection;
import uk.firedev.firefly.modules.small.LootChestProtection;
import uk.firedev.firefly.modules.teleportation.TeleportManager;
import uk.firedev.firefly.modules.titles.TitleManager;

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
                    MessageConfig.getInstance().getMainCommandModulesMessage().applyReplacer(getModulesReplacer()).sendMessage(sender);
                });
    }

    private ComponentReplacer getModulesReplacer() {
        return ComponentReplacer.componentReplacer(
                "customCommands", CustomCommandsManager.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled",
                "elevatorsEnabled", ElevatorManager.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled",
                "kitsEnabled", KitManager.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled",
                "nicknamesEnabled", NicknameManager.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled",
                "playtimeEnabled", PlaytimeManager.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled",
                "amethystProtectEnabled", AmethystProtection.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled",
                "lootChestProtectionEnabled", LootChestProtection.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled",
                "teleportationEnabled", TeleportManager.getInstance().isLoaded() ?  "<green>Enabled" : "<red>Disabled",
                "titlesEnabled", TitleManager.getInstance().isLoaded() ? "<green>Enabled" : "<red>Disabled"
        );
    }

}
