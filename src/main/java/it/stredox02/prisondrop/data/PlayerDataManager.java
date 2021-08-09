package it.stredox02.prisondrop.data;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerDataManager {

    private final List<PlayerData> dataList;

    public PlayerDataManager() {
        dataList = new ArrayList<>();
    }

    public PlayerData addData(Player player) {
        PlayerData data = new PlayerData(player);
        dataList.add(data);
        return data;
    }

    public void removeData(Player player) {
        PlayerData data = getData(player);
        if (data != null) {
            dataList.remove(data);
        }
    }

    public PlayerData getData(Player player) {
        for (PlayerData data : dataList) {
            if (data.getPlayer() == player) {
                return data;
            }
        }
        return null;
    }

}
