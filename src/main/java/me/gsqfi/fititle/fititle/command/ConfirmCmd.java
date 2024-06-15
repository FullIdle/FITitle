package me.gsqfi.fititle.fititle.command;

import com.google.common.collect.Lists;
import me.gsqfi.fititle.fititle.data.CacheData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfirmCmd extends ICmd{
    public ConfirmCmd(ICmd superCmd) {
        super("confirm", superCmd);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        ArrayList<String> list = new ArrayList<>();
        if (SetCmd.map.containsKey(sender)){
            Map.Entry<String, String> entry = SetCmd.map.get(sender);
            String name = entry.getKey();
            String value = entry.getValue();
            CacheData.playerData.setNowPlayerTitle(name, value);
            list.add("§a"+ name+"set now title:"+ value);
            SetCmd.map.remove(sender);
        }
        if (RemoveCmd.map.containsKey(sender)){
            Map.Entry<String, String> entry = RemoveCmd.map.get(sender);
            String name = entry.getKey();
            String title = entry.getValue();
            List<String> titles = CacheData.playerData.getPlayerTitles(name);
            titles.remove(title);
            CacheData.playerData.setPlayerTitles(name,titles.toArray(new String[0]));
            list.add("§a"+name+"remove title:"+title);
            RemoveCmd.map.remove(sender);
        }
        if (list.isEmpty()) {
            sender.sendMessage("§cNo actions to confirm!");
            return false;
        }
        sender.sendMessage(list.toArray(new String[0]));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
