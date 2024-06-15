package me.gsqfi.fititle.fititle.data.playerdata;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import me.gsqfi.fititle.fititle.data.CacheData;
import me.gsqfi.fititle.fititle.events.PlayerTitleChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class YamlPlayerData implements IPlayerData {
    private final File file;
    private final FileConfiguration config;

    /*
    *
    * player_name:
    *   titles:
    *     - ''
    *   now_title: ''
    *
    *
    * */

    @SneakyThrows
    public YamlPlayerData() {
        this.file = new File(CacheData.plugin.getDataFolder(),"player_data.yml");
        if (!this.file.exists()){
            File parentFile = this.file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            this.file.createNewFile();
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    @Override
    public List<String> getPlayerTitles(String playerName) {
        List<String> list = this.config.getStringList(playerName + ".titles");
        if (!list.contains(CacheData.defaultTitle)) {
            list.add(CacheData.defaultTitle);
        }
        return list;
    }

    @Override
    public String getNowPlayerTitle(String playerName) {
        String title = this.config.getString(playerName + ".now_title");
        return title == null ? CacheData.defaultTitle : title;
    }

    @Override
    public void setPlayerTitles(String playerName, String[] titles) {
        PlayerTitleChangeEvent event = new PlayerTitleChangeEvent(PlayerTitleChangeEvent.Type.ALL,playerName, getNowPlayerTitle(playerName), titles);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        titles = event.getNewTitle();
        ArrayList<String> value = Lists.newArrayList(titles);
        value.remove(CacheData.defaultTitle);
        this.config.set(playerName+".titles", value);
        this.save();
    }

    @Override
    public void setNowPlayerTitle(String playerName, String value) {
        PlayerTitleChangeEvent event = new PlayerTitleChangeEvent(PlayerTitleChangeEvent.Type.NOW,playerName, getNowPlayerTitle(playerName), value);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        value = event.getNewTitle()[0];

        if (value.equals(CacheData.defaultTitle)) value = null;
        System.out.println(value == null);
        List<String> list = getPlayerTitles(playerName);
        if (value != null && !list.contains(value)) {
            list.add(value);
        }

        this.setPlayerTitles(playerName,list.toArray(new String[0]));
        this.config.set(playerName+".now_title",value);
        this.save();
    }

    @SneakyThrows
    @Override
    public void save() {
        this.config.save(this.file);
    }

    @Override
    public void release() {
        //
    }
}
