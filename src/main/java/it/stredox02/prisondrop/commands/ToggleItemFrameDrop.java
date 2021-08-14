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
public class ToggleItemFrameDrop implements CommandExecutor {

    private PrisonDrop prisonDrop;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("prisondrop.toggleitemframe")) {
            player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.nopermission")));
            return false;
        }
        PlayerData data = prisonDrop.getPlayerDataManager().getDataMap().get(player.getUniqueId());
        if (data.isItemFrameDrop()) {
            data.setItemFrameDrop(false);
            prisonDrop.getDatabase().updateItemFrameStatus(player, data.isItemFrameDrop());
            player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.toggle-itemframe-set.false")));
        } else {
            data.setItemFrameDrop(true);
            prisonDrop.getDatabase().updateItemFrameStatus(player, data.isItemFrameDrop());
            player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.toggle-itemframe-set.true")));
        }
        return false;
    }

}
