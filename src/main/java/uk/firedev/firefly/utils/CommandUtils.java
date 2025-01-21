package uk.firedev.firefly.utils;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;

public class CommandUtils {

    public static void unregisterCommand(@NotNull String name) {
        CommandAPI.unregister(name);
        CommandAPI.unregister("firefly:" + name);
    }

}
