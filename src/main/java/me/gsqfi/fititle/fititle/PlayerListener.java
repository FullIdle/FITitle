package me.gsqfi.fititle.fititle;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onQuitServer(PlayerQuitEvent e){
        Player player = e.getPlayer();
        Papi.animationCache.remove(player,null);
    }
}
