package uk.firedev.firefly.modules.kit.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.command.argument.ArgumentBase;
import uk.firedev.firefly.modules.kit.Kit;
import uk.firedev.firefly.modules.kit.KitModule;

import java.util.List;
import java.util.function.BiPredicate;

public class KitArgument implements ArgumentBase<Kit, String> {

    private static final DynamicCommandExceptionType UNKNOWN_KIT = new DynamicCommandExceptionType(name ->
        MessageComponentSerializer.message().serialize(Component.text("Unknown Kit: " + name))
    );
    private static final DynamicCommandExceptionType CANNOT_USE = new DynamicCommandExceptionType(name ->
        MessageComponentSerializer.message().serialize(Component.text("Cannot Use Kit: " + name))
    );

    private final BiPredicate<Player, Kit> predicate;

    private KitArgument(@NonNull BiPredicate<Player, Kit> predicate) {
        this.predicate = predicate;
    }

    public static KitArgument create() {
        return new KitArgument((player, kit) -> true);
    }

    public static KitArgument create(@NonNull BiPredicate<Player, Kit> predicate) {
        return new KitArgument(predicate);
    }

    @Override
    public List<String> getSuggestions(@NonNull CommandContext<CommandSourceStack> commandContext) {
        CommandSender sender = commandContext.getSource().getSender();
        return KitModule.getInstance().getKits().values().stream()
            .filter(kit -> {
                if (!(sender instanceof Player player)) {
                    return true;
                }
                return predicate.test(player, kit);
            })
            .map(Kit::getName)
            .toList();
    }

    @Override
    public <S> Kit convert(final String nativeType, final S source) throws CommandSyntaxException {
        if (!(source instanceof CommandSourceStack stack)) {
            throw new IllegalArgumentException("Provided source is not instance of CommandSourceStack!");
        }
        CommandSender sender = stack.getSender();
        Kit kit = KitModule.getInstance().getKit(nativeType);
        if (kit == null) {
            throw UNKNOWN_KIT.create(nativeType);
        }
        if (sender instanceof Player player && !predicate.test(player, kit)) {
            throw CANNOT_USE.create(nativeType);
        }
        return kit;
    }

    /**
     * Gets the native type that this argument uses,
     * the type that is sent to the client.
     *
     * @return native argument type
     */
    @NonNull
    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

    // Overridden stuff we don't need.
    @Override
    public Kit convert(String nativeType) {
        return null;
    }

}
