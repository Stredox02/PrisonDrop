package it.stredox02.prisondrop.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class PlayerData {

    private Player player;
    private boolean deathDrop;
    private boolean moveInventory;
    private boolean itemFrameDrop;
    private boolean globalDrop;
    private ItemStack pickaxe;

    public PlayerData(Player player) {
        this.player = player;
    }

}
