package it.stredox02.prisondrop;

import it.stredox02.prisondrop.commands.ToggleDeathDrop;
import it.stredox02.prisondrop.commands.ToggleGlobalDrop;
import it.stredox02.prisondrop.commands.ToggleInventoryClick;
import it.stredox02.prisondrop.data.PlayerDataManager;
import it.stredox02.prisondrop.database.Database;
import it.stredox02.prisondrop.database.DatabaseType;
import it.stredox02.prisondrop.database.impl.sqlite.SQLiteDatabase;
import it.stredox02.prisondrop.listener.PlayerListener;
import it.stredox02.prisondrop.utils.JarUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Optional;

public class PrisonDrop extends JavaPlugin {

    @Getter
    private Database database;
    @Getter
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        Optional<DatabaseType> dbType = Arrays.stream(DatabaseType.values()).filter(databaseType -> databaseType.name()
                .equalsIgnoreCase(getConfig().getString("database"))).findFirst();
        if (!dbType.isPresent()) {
            Bukkit.getConsoleSender().sendMessage("§cDatabase type error (NOT FOUND)");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (dbType.get() == DatabaseType.SQLITE) {
            File libFolder = new File(getDataFolder() + "/libs");
            if (!libFolder.exists()) {
                libFolder.mkdir();
            }
            Optional<String> sqLiteLib = Arrays.stream(libFolder.list()).filter(s -> s.equalsIgnoreCase("sqlite.jar")).findFirst();
            if (!sqLiteLib.isPresent()) {
                try {
                    URL url = new URL("https://www.stredox02.me/bukkit/libraries/sqlite.jar");
                    try (InputStream in = url.openStream()) {
                        Files.copy(in, new File(getDataFolder() + "/libs/sqlite.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                File file = new File(getDataFolder() + "/libs/sqlite.jar");
                try {
                    File[] libs = new File[]{file};
                    for (File lib : libs) {
                        if (!lib.exists()) {
                            JarUtils.extractFromJar(lib.getName(), lib.getAbsolutePath());
                        }
                    }
                    for (File lib : libs) {
                        if (!lib.exists()) {
                            Bukkit.getConsoleSender().sendMessage("§c" + lib.getName() + " not found, disabling...");
                            Bukkit.getServer().getPluginManager().disablePlugin(this);
                            return;
                        }
                        JarUtils.addClassPath(JarUtils.getJarUrl(lib));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            database = new SQLiteDatabase(this);
            database.init();
        }
        playerDataManager = new PlayerDataManager();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        loadCommands();
    }

    @Override
    public void onDisable() {

    }

    private void loadCommands() {
        getCommand("toggledeath").setExecutor(new ToggleDeathDrop(this));
        getCommand("toggleinvclick").setExecutor(new ToggleInventoryClick(this));
        getCommand("toggledrop").setExecutor(new ToggleGlobalDrop(this));
    }

}
