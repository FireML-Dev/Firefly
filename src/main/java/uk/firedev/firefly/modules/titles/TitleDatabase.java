package uk.firedev.firefly.modules.titles;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.database.FireflyDatabaseModule;
import uk.firedev.firefly.database.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TitleDatabase implements FireflyDatabaseModule {

    private static TitleDatabase instance;

    private final Database database;

    private TitleDatabase() {
        this.database = Firefly.getInstance().getDatabase();
    }

    public static TitleDatabase getInstance() {
        if (instance == null) {
            instance = new TitleDatabase();
        }
        return instance;
    }

    @Override
    public void init() {
        try {
            database.addColumn("firefly_players", "prefix", "varchar");
            database.addColumn("firefly_players", "suffix", "varchar");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save() {}

    @Override
    public void load(@NotNull PlayerData data, @NotNull ResultSet set) throws SQLException {
        String prefix = set.getString("prefix");
        if (prefix != null) {
            data.setPrefix(ComponentMessage.fromString(prefix));
        }
        String suffix = set.getString("suffix");
        if (suffix != null) {
            data.setSuffix(ComponentMessage.fromString(suffix));
        }
    }

    @Override
    public void save(@NotNull PlayerData data, @NotNull Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE firefly_players SET prefix = ?, suffix = ? WHERE uuid = ?")) {
            // Set Prefix
            ComponentMessage prefix = data.getPrefix();
            String prefixStr = null;
            if (prefix != null) {
                prefixStr = prefix.toStringMessage().getMessage();
            }
            ps.setString(1, prefixStr);

            // Set Suffix
            ComponentMessage suffix = data.getSuffix();
            String suffixStr = null;
            if (suffix != null) {
                suffixStr = suffix.toStringMessage().getMessage();
            }
            ps.setString(2, suffixStr);

            // Set UUID and save
            ps.setString(3, data.getUuid().toString());
            ps.executeUpdate();
        }
    }

}
