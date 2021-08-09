package it.stredox02.prisondrop.listener;

import it.stredox02.prisondrop.PrisonDrop;
import it.stredox02.prisondrop.data.PlayerData;
import it.stredox02.prisondrop.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;

@AllArgsConstructor
public class PlayerListener implements Listener {

    private PrisonDrop prisonDrop;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        prisonDrop.getDatabase().loadPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        prisonDrop.getPlayerDataManager().addData(event.getPlayer());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (event.getItemDrop().getItemStack().getType() != Material.DIAMOND_PICKAXE) {
            return;
        }
        PlayerData data = prisonDrop.getPlayerDataManager().getData(player);
        if (!data.isGlobalDrop()) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.cant-drop-pickaxe")));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerData data = prisonDrop.getPlayerDataManager().getData(player);
        if (!data.isDeathDrop()) {
            return;
        }
        event.getDrops().forEach(itemStack -> {
            if (itemStack.getType() == Material.DIAMOND_PICKAXE) {
                data.setPickaxe(itemStack);
            }
        });
        event.getDrops().removeIf(itemStack -> itemStack.getType() == Material.DIAMOND_PICKAXE);
        player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.death-message")));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerData data = prisonDrop.getPlayerDataManager().getData(player);
        if (!data.isDeathDrop()) {
            return;
        }
        player.getInventory().addItem(data.getPickaxe());
        player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.kept-pickaxe")));
    }

    @EventHandler
    public void onInventoryMove(InventoryInteractEvent event) {
        Bukkit.broadcastMessage("InventoryInteractEvent");

        Player player = (Player) event.getWhoClicked();
        PlayerData data = prisonDrop.getPlayerDataManager().getData(player);
        if (event.getInventory().getType() != InventoryType.PLAYER) {
            return;
        }
        if (!data.isMoveInventory()) {
            return;
        }
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
        player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.cant-move-pickaxe")));
    }

    @EventHandler
    public void onInventoryMove(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData data = prisonDrop.getPlayerDataManager().getData(player);
        if (!data.isMoveInventory()) {
            return;
        }
        if (event.getCursor() == null || event.getCurrentItem() == null ||
                (event.getHotbarButton() < 0 || player.getInventory().getItem(event.getHotbarButton()) == null) &&
                        (event.getHotbarButton() < 0 || event.getClickedInventory().getItem(event.getHotbarButton()) == null)) {
            return;
        }
        if (event.getCursor().getType() != Material.DIAMOND_PICKAXE &&
                event.getCurrentItem().getType() != Material.DIAMOND_PICKAXE &&
                player.getInventory().getItem(event.getHotbarButton()).getType() != Material.DIAMOND_PICKAXE) {
            return;
        }
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
        player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.cant-move-pickaxe")));
    }

    @EventHandler
    public void onInventoryMove(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData data = prisonDrop.getPlayerDataManager().getData(player);
        if (event.getInventory().getType() != InventoryType.PLAYER) {
            return;
        }
        if (!data.isMoveInventory()) {
            return;
        }
        if (event.getCursor() == null || event.getOldCursor() == null) {
            return;
        }
        if (event.getCursor().getType() != Material.DIAMOND_PICKAXE &&
                event.getOldCursor().getType() != Material.DIAMOND_PICKAXE) {
            return;
        }
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
        player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.cant-move-pickaxe")));
    }

    @EventHandler
    public void onInteractWithPickaxe(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        Player player = event.getPlayer();
        player.performCommand(prisonDrop.getConfig().getString("right-click-command-with-pickaxe"));
    }

    @EventHandler
    public void onInteractWithPainting(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME) {
            return;
        }
        Player player = event.getPlayer();
        PlayerData data = prisonDrop.getPlayerDataManager().getData(player);
        if (!data.isGlobalDrop() || !data.isMoveInventory()) {
            return;
        }
        if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInOffHand() == null){
            return;
        }
        if(player.getInventory().getItemInMainHand().getType() != Material.DIAMOND_PICKAXE &&
                player.getInventory().getItemInOffHand().getType() != Material.DIAMOND_PICKAXE){
            return;
        }
        event.setCancelled(true);
        player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.pick-on-item-frame")));
    }

}
