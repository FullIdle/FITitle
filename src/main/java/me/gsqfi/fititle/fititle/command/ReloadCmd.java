package me.gsqfi.fititle.fititle.command;

import me.gsqfi.fititle.fititle.data.CacheData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCmd extends ICmd{
    public ReloadCmd(ICmd superCmd) {
        super("reload", superCmd);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        CacheData.plugin.reloadConfig();
        commandSender.sendMessage("§aReloaded!");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
