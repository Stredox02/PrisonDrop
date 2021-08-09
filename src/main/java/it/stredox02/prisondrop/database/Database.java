package it.stredox02.prisondrop.database;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class Database {

    private final DatabaseType databaseType;

    public Database(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public abstract Connection getConnection();

    public abstract void init();

    public abstract void close(Connection connection, PreparedStatement statement, ResultSet resultSet);

    public abstract void loadPlayer(Player player);

    public abstract void insertPlayer(Player player);

    public abstract boolean isPlayerInDatabase(Player player);

    public abstract boolean updateDeathDropStatus(Player player, boolean status);

    public abstract boolean updateInventoryMoveStatus(Player player, boolean status);

    public abstract boolean updateGlobalDropStatus(Player player, boolean status);

    public abstract boolean updateItemFrameStatus(Player player, boolean status);

}
