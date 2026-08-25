package uk.firedev.firefly.modules.economy;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import uk.firedev.daisylib.external.vault.SimpleEconomy;
import uk.firedev.firefly.database.PlayerData;

import java.util.List;

public class FireflyEconomy extends SimpleEconomy {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "FireflyEconomy";
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double amount) {
        return EconomyConfig.getInstance().format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return EconomyConfig.getInstance().getNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return EconomyConfig.getInstance().getNameSingular();
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        try {
            PlayerData.playerData(player.getUniqueId());
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return PlayerData.playerData(player.getUniqueId()).getBalance();
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return PlayerData.playerData(player.getUniqueId()).getBalance() >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Negative values can not be passed to #withdrawPlayer.");
        }
        PlayerData data = PlayerData.playerData(player.getUniqueId());
        double balance = data.getBalance();
        if (amount > balance) {
            return new EconomyResponse(
                0,
                balance,
                EconomyResponse.ResponseType.FAILURE,
                "Not enough money."
            );
        }
        balance = data.decrementBalance(amount);
        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Negative values can not be passed to #depositPlayer.");
        }
        PlayerData data = PlayerData.playerData(player.getUniqueId());
        double balance = data.incrementBalance(amount);
        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return true;
    }

    // Crap we don't use.

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

}
