package me.gsqfi.fititle.fititle.command;

import me.gsqfi.fititle.fititle.data.CacheData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCmd extends ICmd{
    public HelpCmd(ICmd superCmd) {
        super("help", superCmd);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(CacheData.help);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
