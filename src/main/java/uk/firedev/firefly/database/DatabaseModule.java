package uk.firedev.firefly.database;

public interface DatabaseModule {

    void init();

    void save();

    default void register() {
        if (Database.getInstance().getConnection() == null) {
            throw new RuntimeException("Tried to load a DatabaseModule class before the Database class was loaded!");
        }
        Database.getInstance().registerModule(this);
    }

}
