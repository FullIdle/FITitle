package me.gsqfi.fititle.fititle;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onQuitServer(PlayerQuitEvent e){
        Papi.animationCache.remove(e.getPlayer().getUniqueId(),null);
    }
}
