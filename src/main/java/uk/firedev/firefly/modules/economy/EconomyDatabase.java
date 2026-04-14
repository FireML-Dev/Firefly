package uk.firedev.firefly.modules.economy;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.database.DatabaseModule;
import uk.firedev.daisylib.util.Loggers;
import uk.firedev.daisylib.util.ReadOnlyPair;
import uk.firedev.daisylib.util.Utils;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.database.FireflyDatabaseModule;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.economy.baltop.BaltopEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

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

    public CompletableFuture<Stream<BaltopEntry>> fetchBaltop() {
        Comparator<BaltopEntry> comparator = Comparator.comparingDouble(BaltopEntry::balance).reversed();
        Database db = Firefly.getInstance().getDatabase();

        String sql = "SELECT * FROM firefly_players ORDER BY (balance + 0) DESC LIMIT " + EconomyConfig.getInstance().getBaltopEntries();

        return CompletableFuture.supplyAsync(() -> {
            List<BaltopEntry> list = new ArrayList<>();
            try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    BaltopEntry entry = parseBaltop(rs);
                    if (entry != null) {
                        list.add(entry);
                    }
                }
            } catch (SQLException exception) {
                Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to fetch baltop.", exception);
                return Stream.of();
            }
            return list.stream().sorted(comparator);
        });
    }

    private @Nullable BaltopEntry parseBaltop(@NonNull ResultSet rs) throws SQLException {
        String uuidStr = rs.getString("uuid");
        String balanceStr = rs.getString("balance");
        try {
            UUID uuid = UUID.fromString(uuidStr);
            double balance = Double.parseDouble(balanceStr);
            return new BaltopEntry(uuid, balance);
        } catch (NullPointerException | IllegalArgumentException exception) {
            return null;
        }
    }

}
