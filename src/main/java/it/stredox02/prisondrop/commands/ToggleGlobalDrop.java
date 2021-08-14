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
public class ToggleGlobalDrop implements CommandExecutor {

    private PrisonDrop prisonDrop;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("prisondrop.toggledrop")) {
            player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.nopermission")));
            return false;
        }
        PlayerData data = prisonDrop.getPlayerDataManager().getDataMap().get(player.getUniqueId());
        if (data.isGlobalDrop()) {
            data.setGlobalDrop(false);
            data.setMoveInventory(false);
            data.setDeathDrop(false);
            data.setItemFrameDrop(false);
            prisonDrop.getDatabase().updateGlobalDropStatus(player, data.isGlobalDrop());
            player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.toggle-death-set.false")));
        } else {
            data.setGlobalDrop(true);
            data.setMoveInventory(true);
            data.setDeathDrop(true);
            data.setItemFrameDrop(true);
            prisonDrop.getDatabase().updateGlobalDropStatus(player, data.isGlobalDrop());
            player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.toggle-death-set.true")));
        }
        return false;
    }

}
