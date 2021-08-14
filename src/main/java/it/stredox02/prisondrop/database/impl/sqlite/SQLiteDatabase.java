package it.stredox02.prisondrop.database.impl.sqlite;

import it.stredox02.prisondrop.PrisonDrop;
import it.stredox02.prisondrop.data.PlayerData;
import it.stredox02.prisondrop.database.Database;
import it.stredox02.prisondrop.database.DatabaseType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLiteDatabase extends Database {

    private final PrisonDrop prisonDrop;
    private Connection connection;

    public SQLiteDatabase(PrisonDrop prisonDrop) {
        super(DatabaseType.SQLITE);
        this.prisonDrop = prisonDrop;
    }

    @Override
    public Connection getConnection() {
        File file = new File(prisonDrop.getDataFolder(), "database.db");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public void close(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        if (connection != null) try {
            connection.close();
        } catch (Exception ignored) {
        }
        if (statement != null) try {
            statement.close();
        } catch (Exception ignored) {
        }
        if (resultSet != null) try {
            resultSet.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void loadPlayer(Player player) {
        PlayerData data = prisonDrop.getPlayerDataManager().getDataMap().get(player.getUniqueId()) == null ?
                prisonDrop.getPlayerDataManager().addData(player) :
                prisonDrop.getPlayerDataManager().getDataMap().get(player.getUniqueId());
        data.setGlobalDrop(true);
        data.setDeathDrop(true);
        data.setMoveInventory(true);
        data.setItemFrameDrop(true);
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if(!isPlayerInDatabase(player)){
            insertPlayer(player);
        } else {
            try {
                connection = getConnection();
                statement = connection.prepareStatement("SELECT * FROM `players` WHERE `uuid` = ?");
                statement.setString(1, player.getUniqueId().toString());
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    if(resultSet.getBoolean("drop")){
                        data.setGlobalDrop(true);
                        data.setDeathDrop(true);
                        data.setMoveInventory(true);
                        data.setItemFrameDrop(true);
                    } else {
                        data.setDeathDrop(resultSet.getBoolean("death"));
                        data.setMoveInventory(resultSet.getBoolean("inventory"));
                        data.setItemFrameDrop(resultSet.getBoolean("itemframe"));
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                close(connection, statement, resultSet);
            }
        }
    }

    @Override
    public void insertPlayer(Player player) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(
                    "INSERT INTO `players` (`uuid`, `drop`, `inventory`, `death`, `itemframe`) VALUES" +
                            "(?,?,?,?,?)");
            statement.setString(1,player.getUniqueId().toString());
            statement.setBoolean(2,true);
            statement.setBoolean(3,true);
            statement.setBoolean(4,true);
            statement.setBoolean(5,true);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close(connection, statement, resultSet);
        }
    }


    @Override
    public void init() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `players` (`uuid` varchar(32), `drop` int(1)," +
                            " `inventory` int(1), `death` int(1), `itemframe` int(1), PRIMARY KEY (`uuid`));");
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close(connection, statement, resultSet);
        }
        Bukkit.getServer().getConsoleSender().sendMessage("§aPlugin initialized");
    }

    @Override
    public boolean isPlayerInDatabase(Player player) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement("SELECT `uuid` FROM `players` WHERE `uuid` = ?");
            statement.setString(1, player.getUniqueId().toString());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString("uuid").equals(player.getUniqueId().toString());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close(connection, statement, resultSet);
        }
        return false;
    }

    @Override
    public boolean updateDeathDropStatus(Player player, boolean status) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement("UPDATE `players` SET `death` = ? WHERE `uuid` = ?");
            statement.setBoolean(1, status);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close(connection, statement, resultSet);
        }
        return false;
    }

    @Override
    public boolean updateInventoryMoveStatus(Player player, boolean status) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement("UPDATE `players` SET `inventory` = ? WHERE `uuid` = ?");
            statement.setBoolean(1, status);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close(connection, statement, resultSet);
        }
        return false;
    }

    @Override
    public boolean updateGlobalDropStatus(Player player, boolean status) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement("UPDATE `players` SET `drop` = ?, `death` = ?, `inventory` = ?, `itemframe` = ? WHERE `uuid` = ?");
            statement.setBoolean(1, status);
            statement.setBoolean(2, status);
            statement.setBoolean(3, status);
            statement.setBoolean(4, status);
            statement.setString(5, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close(connection, statement, resultSet);
        }
        return false;
    }

    @Override
    public boolean updateItemFrameStatus(Player player, boolean status) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement("UPDATE `players` SET `itemframe` = ? WHERE `uuid` = ?");
            statement.setBoolean(1, status);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close(connection, statement, resultSet);
        }
        return false;
    }

}
