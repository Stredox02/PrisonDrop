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
public class ToggleDeathDrop implements CommandExecutor {

    private PrisonDrop prisonDrop;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("prisondrop.toggledeath")) {
            player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.nopermission")));
            return false;
        }
        PlayerData data = prisonDrop.getPlayerDataManager().getDataMap().get(player.getUniqueId());
        if (data.isDeathDrop()) {
            data.setDeathDrop(false);
            prisonDrop.getDatabase().updateDeathDropStatus(player, data.isDeathDrop());
            player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.toggle-death-set.false")));
        } else {
            data.setDeathDrop(true);
            prisonDrop.getDatabase().updateDeathDropStatus(player, data.isDeathDrop());
            player.sendMessage(Utils.colorize(prisonDrop.getConfig().getString("language.toggle-death-set.true")));
        }
        return false;
    }

}
