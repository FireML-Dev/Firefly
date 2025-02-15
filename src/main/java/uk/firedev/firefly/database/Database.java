package uk.firedev.firefly.database;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.database.SQLiteDatabase;
import uk.firedev.daisylib.api.database.exceptions.DatabaseLoadException;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MainConfig;

import java.util.HashMap;
import java.util.Map;

public class Database extends SQLiteDatabase {

    private final Map<String, String> columns = new HashMap<>();

    public Database(@NotNull Firefly firefly) {
        super(firefly);
        columns.put("uuid", "VARCHAR(36) NOT NULL PRIMARY KEY");
    }

    @Override
    public void load() throws DatabaseLoadException {
        super.load();
        Bukkit.getPluginManager().registerEvents(new DatabaseListener(), getPlugin());
    }

    @Override
    public void save() {}

    @NotNull
    @Override
    public String getTable() {
        return "firefly_players";
    }

    @Override
    public @NotNull Map<String, String> getColumns() {
        return columns;
    }

    @Override
    public long getAutoSaveSeconds() {
        return MainConfig.getInstance().getDatabaseSaveInterval();
    }

}
