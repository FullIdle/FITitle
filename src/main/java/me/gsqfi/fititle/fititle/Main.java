package me.gsqfi.fititle.fititle;

import me.clip.placeholderapi.PlaceholderAPI;
import me.gsqfi.fititle.fititle.command.MainCmd;
import me.gsqfi.fititle.fititle.data.CacheData;
import me.gsqfi.fititle.fititle.data.playerdata.IPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void reloadConfig() {
        saveDefaultConfig();
        super.reloadConfig();
        CacheData.init();
    }

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskTimer(this,()->{
            System.out.println(PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer("GSQ_Lin"), "%fititle_now%"));
        },0,5);

        CacheData.plugin = this;
        this.reloadConfig();
        new Papi(this).register();
        MainCmd mainCmd = new MainCmd();
        PluginCommand command = this.getCommand("fititle");
        command.setExecutor(mainCmd);
        command.setTabCompleter(mainCmd);
        getLogger().info("§aPlugin loaded!");
    }

    @Override
    public void onDisable() {
        IPlayerData playerData = CacheData.playerData;
        playerData.save();
    }
}
