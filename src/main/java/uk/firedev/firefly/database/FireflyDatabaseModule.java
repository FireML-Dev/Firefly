package uk.firedev.firefly.database;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.database.DatabaseModule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface FireflyDatabaseModule extends DatabaseModule {

    void load(@NotNull PlayerData data, @NotNull ResultSet set) throws SQLException;

    void save(@NotNull PlayerData data, @NotNull Connection connection) throws SQLException;

}
