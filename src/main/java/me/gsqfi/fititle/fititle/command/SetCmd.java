package me.gsqfi.fititle.fititle.command;

import me.gsqfi.fititle.fititle.data.CacheData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetCmd extends ICmd {
    public SetCmd(ICmd superCmd) {
        super("set", superCmd);
    }

    public static final Map<CommandSender, Map.Entry<String, String>> map = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int length = args.length;
        if (length > 1) {
            Player player = Bukkit.getPlayer(args[0]);
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < length; i++) {
                builder.append(args[i]);
                if (i != length-1){
                    builder.append(" ");
                }
            }

            if (player == null) {
                sender.sendMessage("§c玩家不存在!");
                return false;
            }
            map.put(sender, new AbstractMap.SimpleEntry<>(player.getName(), builder.toString()));
            sender.sendMessage("§aConfirm with /fittitle confirm");
            return false;
        }
        sender.sendMessage(CacheData.help);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        return null;
    }
}
