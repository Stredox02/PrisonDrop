package it.stredox02.prisondrop.commands;

import it.stredox02.prisondrop.PrisonDrop;
import it.stredox02.prisondrop.data.PlayerData;
import it.stredox02.prisondrop.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class ToggleInventoryClick implements CommandExecutor {

    private PrisonDrop prisonDrop;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("prisondrop.toggleinvclick")) {
            player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.nopermission")));
            return false;
        }
        PlayerData data = prisonDrop.getPlayerDataManager().getData(player);
        if (data.isMoveInventory()) {
            data.setMoveInventory(false);
            prisonDrop.getDatabase().updateInventoryMoveStatus(player, data.isMoveInventory());
            player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.toggle-move-set.false")));
        } else {
            data.setMoveInventory(true);
            prisonDrop.getDatabase().updateInventoryMoveStatus(player, data.isMoveInventory());
            player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.toggle-move-set.true")));
        }
        return false;
    }

}
