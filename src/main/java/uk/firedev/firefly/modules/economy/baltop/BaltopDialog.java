package uk.firedev.firefly.modules.economy.baltop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.modules.economy.EconomyConfig;
import uk.firedev.firefly.modules.economy.EconomyDatabase;
import uk.firedev.firefly.utils.dialog.InfoDialogBuilder;

import java.util.stream.Stream;

public class BaltopDialog {

    public static void open(@NonNull Player player) {
        EconomyConfig.getInstance().getBaltopOpeningMessage().send(player);
        EconomyDatabase.getInstance().fetchBaltop().thenAccept(values ->
            Bukkit.getScheduler().runTask(Firefly.getInstance(), () -> new BaltopDialog(player, values).open())
        );
    }

    private final Player player;
    private final InfoDialogBuilder builder;

    private BaltopDialog(@NonNull Player player, @NonNull Stream<BaltopEntry> values) {
        this.player = player;
        this.builder = new InfoDialogBuilder().withTitle(EconomyConfig.getInstance().getBaltopTitleMessage());
        values.forEach(this::loadEntry);
    }

    private void loadEntry(BaltopEntry entry) {
        builder.addContent(EconomyConfig.getInstance().getBaltopEntryMessage(entry));
    }

    private void open() {
        builder.open(player);
    }

}
