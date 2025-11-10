package uk.firedev.firefly.modules.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.libs.messagelib.config.PaperConfigLoader;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.config.MessageConfig;

import java.util.List;

public interface Command extends SubModule, CommandHolder {

    @NotNull String getConfigName();

    default @NotNull String getCommandName() {
        return getConfig().getString("name", getConfigName());
    }

    @Override
    default @NotNull List<String> aliases() {
        return getConfig().getStringList("aliases");
    }

    /**
     * @return This command's description.
     */
    @Nullable
    @Override
    default String description() {
        return null;
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

    @Override
    default void init() {}

    @NotNull LiteralCommandNode<CommandSourceStack> get();

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

    @Override
    default @NotNull String permission() {
        return getConfig().getString("permission", "firefly.command." + getConfigName().toLowerCase());
    }

    @Override
    default @NotNull String targetPermission() {
        return getConfig().getString("target-permission", "firefly.command." + getConfigName().toLowerCase() + ".other");
    }

}
