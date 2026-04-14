package uk.firedev.firefly.modules.economy;

import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.util.Utils;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.database.FireflyDatabaseModule;
import uk.firedev.firefly.database.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EconomyDatabase implements FireflyDatabaseModule {

    private static EconomyDatabase instance;

    private EconomyDatabase() {}

    public static EconomyDatabase getInstance() {
        if (instance == null) {
            instance = new EconomyDatabase();
        }
        return instance;
    }

    @Override
    public void init() {
        try {
            Firefly.getInstance().getDatabase().addColumn("firefly_players", "balance", "varchar");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save() {}

    @Override
    public void load(@NonNull PlayerData data, @NonNull ResultSet set) throws SQLException {
        String balance = set.getString("balance");
        if (balance == null) {
            return;
        }
        Double value = Utils.getDouble(balance);
        if (value != null) {
            data.setBalance(value);
        }
    }

    @Override
    public void save(@NonNull PlayerData data, @NonNull Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE firefly_players SET balance = ? WHERE uuid = ?")) {
            ps.setString(1, String.valueOf(data.getBalance()));
            ps.setString(2, data.getUuid().toString());
            ps.executeUpdate();
        }
    }

}
