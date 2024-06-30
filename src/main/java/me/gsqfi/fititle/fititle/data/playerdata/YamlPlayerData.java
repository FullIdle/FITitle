package me.gsqfi.fititle.fititle.data.playerdata;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import me.gsqfi.fititle.fititle.data.CacheData;
import me.gsqfi.fititle.fititle.events.PlayerTitleChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YamlPlayerData implements IPlayerData {
    private final File file;
    private final FileConfiguration config;

    public YamlPlayerData(String data){
        this.file = null;
        this.config = YamlConfiguration.loadConfiguration(new StringReader(data));
    }

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
    public void setPlayerTitles(String playerName, List<String> titles) {
        PlayerTitleChangeEvent event = new PlayerTitleChangeEvent(PlayerTitleChangeEvent.Type.ALL,playerName, getNowPlayerTitle(playerName), titles);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        titles = event.getNewTitle();
        ArrayList<String> value = Lists.newArrayList(titles);
        value.remove(CacheData.defaultTitle);
        this.config.set(playerName+".titles", value);
    }

    @Override
    public void setNowPlayerTitle(String playerName, String value) {
        PlayerTitleChangeEvent event = new PlayerTitleChangeEvent(PlayerTitleChangeEvent.Type.NOW,playerName, getNowPlayerTitle(playerName), Collections.singletonList(value));
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        value = event.getNewTitle().isEmpty() ? null : event.getNewTitle().get(0);

        if (value.equals(CacheData.defaultTitle)) value = null;
        List<String> list = getPlayerTitles(playerName);
        if (value != null && !list.contains(value)) {
            list.add(value);
        }

        this.setPlayerTitles(playerName,list);
        this.config.set(playerName+".now_title",value);
    }

    @SneakyThrows
    @Override
    public void save() {
        this.config.save(this.file);
    }

    public String saveToString() {
        return this.config.saveToString();
    }
}
