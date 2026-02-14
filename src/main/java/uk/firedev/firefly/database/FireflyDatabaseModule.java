package uk.firedev.firefly.database;

import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.database.DatabaseModule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface FireflyDatabaseModule extends DatabaseModule {

    void load(@NonNull PlayerData data, @NonNull ResultSet set) throws SQLException;

    void save(@NonNull PlayerData data, @NonNull Connection connection) throws SQLException;

}
