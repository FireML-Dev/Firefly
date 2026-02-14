package uk.firedev.firefly;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface CommandHolder {

    default void initCommand() {
        Firefly.getInstance().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(get(), description(), aliases());
        });
    }

    /**
     * @return The command.
     */
    @NonNull LiteralCommandNode<CommandSourceStack> get();

    /**
     * @return The list of aliases this command should have.
     */
    @NonNull List<String> aliases();

    /**
     * @return The permission for executing this command on yourself.
     */
    @NonNull String permission();

    /**
     * @return The permission for executing this command on another player.
     */
    @NonNull String targetPermission();

    /**
     * @return This command's description.
     */
    @Nullable String description();

}
