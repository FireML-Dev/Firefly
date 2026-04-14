package uk.firedev.firefly.modules.economy;

import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.util.Loggers;
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

    public ComponentMessage getBalanceMessage(@NonNull OfflinePlayer player) {
        PlayerData data = PlayerData.playerData(player.getUniqueId());
        return getComponentMessage("messages.balance", "<#F0E68C>{player}'s Balance: {balance}")
            .replace("{player}", data.getNickname())
            .replace("{balance}", format(data.getBalance()));
    }

}
