package uk.firedev.firefly.modules.economy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.util.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.economy.baltop.BaltopEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Optional;

public class EconomyConfig extends ConfigBase {

    private static EconomyConfig instance;

    private EconomyConfig() {
        super("modules/economy.yml", "modules/economy.yml", Firefly.getInstance());
    }

    public static EconomyConfig getInstance() {
        if (instance == null) {
            instance = new EconomyConfig();
        }
        return instance;
    }

    public String format(double value) {
        String format = getConfig().getString("format", "$#,##0.0");
        try {
            DecimalFormat df = new DecimalFormat(format);
            return df.format(value);
        } catch (IllegalArgumentException exception) {
            Loggers.warn(Firefly.getInstance().getLogger(), "Economy format is invalid: " + format + ". Using the default.");
            DecimalFormat df = new DecimalFormat("$#,##0.0");
            return df.format(value);
        }
    }

    public String getNameSingular() {
        return getConfig().getString("name.singular", "Dollar");
    }

    public String getNamePlural() {
        return getConfig().getString("name.plural", "Dollars");
    }

    public int getBaltopEntries() {
        return getConfig().getInt("baltop-entries", 20);
    }

    public ComponentMessage getBalanceMessage(@NonNull OfflinePlayer player) {
        PlayerData data = PlayerData.playerData(player.getUniqueId());
        return getComponentMessage("messages.balance", "{prefix}<#F0E68C>{player}'s Balance: {balance}")
            .replace("{player}", data.getNickname())
            .replace("{balance}", format(data.getBalance()));
    }

    public ComponentMessage getNotEnoughMoneyMessage(double amount) {
        return getComponentMessage("messages.not-enough-money", "{prefix}<red>You do not have {amount}!")
            .replace("{amount}", format(amount));
    }

    public ComponentMessage getTargetNotEnoughMoneyMessage(@NonNull PlayerData playerData, double amount) {
        return getComponentMessage("messages.target-not-enough-money", "{prefix}<red>{player} does not have {amount}.")
            .replace("{player}", playerData.getNickname())
            .replace("{amount}", format(amount));
    }

    // /pay

    public ComponentMessage getPaySendMessage(@NonNull PlayerData targetData, double amount) {
        return getComponentMessage("messages.pay.send", "{prefix}<#F0E68C>You have sent {amount} to {target}.")
            .replace("{amount}", format(amount))
            .replace("{target}", targetData.getNickname());
    }

    public ComponentMessage getPayReceiveMessage(@NonNull PlayerData senderData, double amount) {
        return getComponentMessage("messages.pay.receive", "{prefix}<#F0E68C>You have been sent {amount} from {player}.")
            .replace("{amount}", format(amount))
            .replace("{player}", senderData.getNickname());
    }

    // /money

    public ComponentMessage getMoneySetSuccessMessage(@NonNull PlayerData playerData, double amount) {
        return getComponentMessage("messages.money.set.success", "{prefix}<#F0E68C>Set {player}'s balance to {amount}.")
            .replace("{player}", playerData.getNickname())
            .replace("{amount}", format(amount));
    }

    public ComponentMessage getMoneyAddSuccessMessage(@NonNull PlayerData playerData, double amount) {
        return getComponentMessage("messages.money.add.success", "{prefix}<#F0E68C>Added {amount} to {player}'s balance.")
            .replace("{amount}", format(amount))
            .replace("{player}", playerData.getNickname());
    }

    public ComponentMessage getMoneyTakeSuccessMessage(@NonNull PlayerData playerData, double amount) {
        return getComponentMessage("messages.money.take.success", "{prefix}<#F0E68C>Taken {amount} from {player}'s balance.")
            .replace("{amount}", format(amount))
            .replace("{player}", playerData.getNickname());
    }

    public ComponentMessage getMoneyTransferSuccessMessage(@NonNull PlayerData playerData, @NonNull PlayerData targetData, double amount) {
        return getComponentMessage("messages.money.transfer.success", "{prefix}<#F0E68C>Transferred {amount} from {player} to {target}.")
            .replace("{player}", playerData.getNickname())
            .replace("{target}", targetData.getNickname())
            .replace("{amount}", format(amount));
    }

    // /baltop

    public ComponentMessage getBaltopOpeningMessage() {
        return getComponentMessage("messages.baltop.opening", "{prefix}<#F0E68C>Opening /baltop...");
    }

    public ComponentMessage getBaltopTitleMessage() {
        return getComponentMessage("messages.baltop.title", "<#F0E68C>Player Balance Leaderboard");
    }

    public ComponentMessage getBaltopEntryMessage(@NonNull BaltopEntry entry) {
        return getComponentMessage("messages.baltop.entry", "<#F0E68C>{sprite} {player}: <white>{amount}")
            .replace("{sprite}", Component.object(ObjectContents.playerHead(entry.uuid())))
            .replace("{player}", Optional.ofNullable(entry.player().getName()).orElse("N/A"))
            .replace("{amount}", format(entry.balance()));
    }

    @Override
    public ComponentMessage getComponentMessage(@NonNull String path, @NonNull Object def) {
        return super.getComponentMessage(path, def).replace(MessageConfig.getInstance().getPrefixReplacer());
    }

}
