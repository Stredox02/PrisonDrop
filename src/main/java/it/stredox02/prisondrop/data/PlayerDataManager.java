package it.stredox02.prisondrop.data;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerDataManager {

    @Getter private final Map<UUID, PlayerData> dataMap;

    public PlayerDataManager() {
        dataMap = new HashMap<>();
    }

    public PlayerData addData(Player player) {
        PlayerData data = new PlayerData(player);
        dataMap.put(player.getUniqueId(), data);
        return data;
    }

    public void removeData(Player player) {
        dataMap.remove(player.getUniqueId());
    }

}
