package uk.firedev.firefly.modules.economy.baltop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.builders.dialog.DialogBuilder;
import uk.firedev.daisylib.builders.dialog.InformationDialogBuilder;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.modules.economy.EconomyConfig;
import uk.firedev.firefly.modules.economy.EconomyDatabase;

import java.util.stream.Stream;

public class BaltopDialog {

    public static void open(@NonNull Player player) {
        EconomyConfig.getInstance().getBaltopOpeningMessage().send(player);
        EconomyDatabase.getInstance().fetchBaltop().thenAccept(values ->
            Bukkit.getScheduler().runTask(Firefly.getInstance(), () -> new BaltopDialog(player, values).open())
        );
    }

    private final Player player;
    private final InformationDialogBuilder builder;

    private BaltopDialog(@NonNull Player player, @NonNull Stream<BaltopEntry> values) {
        this.player = player;
        this.builder = DialogBuilder.information()
            .withTitle(EconomyConfig.getInstance().getBaltopTitleMessage());
        values.forEach(this::loadEntry);
        //loadJunkEntries();
    }

    private void loadEntry(BaltopEntry entry) {
        if (!entry.valid()) {
            return;
        }
        builder.addContent(EconomyConfig.getInstance().getBaltopEntryMessage(entry));
    }

    private void loadJunkEntries() {
        ComponentMessage message = EconomyConfig.getInstance().getComponentMessage("messages.baltop.entry", "<#F0E68C>{sprite} {player}: <white>{amount}");
        for (int i = 100; i >= 0; i--) {
            builder.addContent(
                message.replace("{sprite}", Component.object(ObjectContents.playerHead().build()))
                    .replace("{player}", "Player" + i)
                    .replace("{amount}", EconomyConfig.getInstance().format(i))
            );
        }
    }

    private void open() {
        builder.open(player);
    }

}
