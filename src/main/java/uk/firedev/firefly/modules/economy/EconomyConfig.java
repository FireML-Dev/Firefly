package uk.firedev.firefly.modules.economy;

import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.database.PlayerData;

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

    public int getDecimalPlaces() {
        return getConfig().getInt("decimal-places", 2);
    }

    public double formatDouble(double value) {
        int places = getDecimalPlaces();
        if (places < 0) {
            return value;
        }
        return new BigDecimal(value)
            .setScale(getDecimalPlaces(), RoundingMode.HALF_UP)
            .doubleValue();
    }

    public String format(double value) {
        return getConfig().getString("format", "${amount}")
            .replace("{amount}", String.valueOf(formatDouble(value)));
    }

    public String getNameSingular() {
        return getConfig().getString("name.singular", "Dollar");
    }

    public String getNamePlural() {
        return getConfig().getString("name.plural", "Dollars");
    }

    public ComponentMessage getBalanceMessage(@NonNull OfflinePlayer player) {
        PlayerData data = PlayerData.playerData(player.getUniqueId());
        return getComponentMessage("messages.balance", "<#F0E68C>{player}'s Balance: {balance}")
            .replace("{player}", data.getNickname())
            .replace("{balance}", format(data.getBalance()));
    }

}
