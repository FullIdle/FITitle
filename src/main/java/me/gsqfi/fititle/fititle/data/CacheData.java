package me.gsqfi.fititle.fititle.data;

import me.gsqfi.fititle.fititle.Main;
import me.gsqfi.fititle.fititle.data.playerdata.IPlayerData;
import me.gsqfi.fititle.fititle.data.playerdata.SqlPlayerData;
import me.gsqfi.fititle.fititle.data.playerdata.YamlPlayerData;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CacheData {
    public static Main plugin;
    public static IPlayerData playerData;
    public static String[] help;
    public static String title_warehouse_title;
    public static Material title_warehouse_material;
    public static String title_warehouse_name;
    public static List<String> title_warehouse_lore;
    public static String defaultTitle;


    public static void init() {
        clear();

        FileConfiguration config = plugin.getConfig();
        if (CacheData.playerData != null) CacheData.playerData.release();
        CacheData.playerData = config.getBoolean("sql.enable") ? new SqlPlayerData() : new YamlPlayerData();
        help = config.getStringList("msg.help").stream().map(s->s.replace('&','§')).toArray(String[]::new);

        title_warehouse_title = config.getString("TitleWarehouse.title").replace('&','§');
        title_warehouse_material = Material.getMaterial(config.getString("TitleWarehouse.material"));
        title_warehouse_name = config.getString("TitleWarehouse.name");
        title_warehouse_lore = config.getStringList("TitleWarehouse.lore");

        defaultTitle = config.getString("defaultTitle");
    }

    public static void clear() {
    }
}
