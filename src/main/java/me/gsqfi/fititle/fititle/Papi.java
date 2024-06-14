package me.gsqfi.fititle.fititle;

import com.google.common.collect.Lists;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.gsqfi.fititle.fititle.data.CacheData;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Papi extends PlaceholderExpansion {
    private final Main plugin;
    public Papi(Main main){
        this.plugin = main;
    }

    public static final Map<OfflinePlayer,Map<String,Integer>> animationCache = new HashMap<>();

    @Override
    public @NotNull String getIdentifier() {
        return this.plugin.getDescription().getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }


    /*
    * %fititle_now%
    * */
    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] split = params.split("_");
        String arg = split[0];
        if (arg.equalsIgnoreCase("now")){
            return PlaceholderAPI.setPlaceholders(player,
                    CacheData.playerData.getNowPlayerTitle(player.getName()).replace('&','§'));
        }
        if (arg.equalsIgnoreCase("animation")){
            Map<String, Integer> map = animationCache.computeIfAbsent(player,k->new HashMap<>());
            int i = map.computeIfAbsent(params, k -> 0);
            ArrayList<String> list = Lists.newArrayList(split);
            list.remove(0);
            i++;
            if (i >= list.size()) i = 0;
            map.put(params,i);
            return list.get(i).replace('&','§');
        }
        return null;
    }
}
