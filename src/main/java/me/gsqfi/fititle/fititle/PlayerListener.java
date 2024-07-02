package me.gsqfi.fititle.fititle;

import me.gsqfi.fititle.fititle.data.CacheData;
import me.gsqfi.fititle.fititle.data.playerdata.SqlPlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onQuitServer(PlayerQuitEvent e){
        Papi.animationCache.remove(e.getPlayer().getUniqueId(),null);
        if (CacheData.playerData instanceof SqlPlayerData) {
            String name = e.getPlayer().getName();
            SqlPlayerData playerData = (SqlPlayerData) CacheData.playerData;
            playerData.player_now_title.remove(name);
            playerData.player_titles.remove(name);
        }
    }
}
