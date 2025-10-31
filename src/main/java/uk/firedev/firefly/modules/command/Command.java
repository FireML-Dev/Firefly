package uk.firedev.firefly.modules.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.messagelib.config.PaperConfigLoader;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.config.MessageConfig;

import java.util.List;

public interface Command extends SubModule {

    @NotNull String getConfigName();

    default @NotNull String getCommandName() {
        return getConfig().getString("name", getConfigName());
    }

    default @NotNull List<String> getAliases() {
        return getConfig().getStringList("aliases");
    }

    default @NotNull ConfigurationSection getConfig() {
        ConfigurationSection section = CommandConfig.getInstance().getConfig().getConfigurationSection(getConfigName());
        if (section == null) {
            return CommandConfig.getInstance().getConfig().createSection(getConfigName());
        }
        return section;
    }

    @Override
    default boolean isConfigEnabled() {
        return getConfig().getBoolean("enabled", true);
    }

    @NotNull LiteralCommandNode<CommandSourceStack> get();

    @Override
    default void init() {
        Firefly.getInstance().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(get(), null, getAliases());
        });
    }

    @Override
    default void reload() {}

    @Override
    default void unload() {}

    default @NotNull ComponentMessage getMessage(@NotNull String path, @NotNull String def) {
        PaperConfigLoader loader = new PaperConfigLoader(getConfig());
        String message = loader.getString("messages." + path);
        return ComponentMessage.componentMessage(message == null ? def : message)
            .replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    default @NotNull String getPermission() {
        return getConfig().getString("permission", "firefly.command." + getConfigName().toLowerCase());
    }

    default @NotNull String getTargetPermission() {
        return getConfig().getString("target-permission", "firefly.command." + getConfigName().toLowerCase() + ".other");
    }

}
