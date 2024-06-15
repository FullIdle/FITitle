package me.gsqfi.fititle.fititle.command;

import com.google.common.collect.Lists;
import me.gsqfi.fititle.fititle.data.CacheData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveCmd extends ICmd{
    public static Map<CommandSender, Map.Entry<String,String>> map = new HashMap<>();

    public RemoveCmd(ICmd superCmd) {
        super("remove", superCmd);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        int length = args.length;
        if (length > 1){
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("§c玩家不存在!");
                return false;
            }
            String name = player.getName();
            String title = args[1];
            if (!CacheData.playerData.getPlayerTitles(name).contains(title)) {
                sender.sendMessage("§cPlayer does not have "+title+" title");
                return false;
            }
            map.put(sender, new AbstractMap.SimpleEntry<>(name, title));
            sender.sendMessage("§aConfirm with /fittitle confirm");
            return false;
        }
        sender.sendMessage(CacheData.help);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 2){
            Player player = Bukkit.getPlayer(args[0]);
            if (player != null) {
                String name = player.getName();
                return CacheData.playerData.getPlayerTitles(name);
            }
        }
        return null;
    }
}
