package me.gsqfi.fititle.fititle.command;

import me.gsqfi.fititle.fititle.gui.TitleWarehouse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class OpenCmd extends ICmd{
    public OpenCmd(ICmd superCmd) {
        super("open", superCmd);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player){
            Player player = (Player) commandSender;
            TitleWarehouse war = new TitleWarehouse(player);
            player.openInventory(war.getInventory());
            return false;
        }
        commandSender.sendMessage("§cYou're not a player!");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
