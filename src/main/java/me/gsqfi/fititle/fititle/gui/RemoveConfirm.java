package me.gsqfi.fititle.fititle.gui;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.fullidle.ficore.ficore.common.api.ineventory.ListenerInvHolder;
import me.gsqfi.fititle.fititle.data.CacheData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

@Getter
public class RemoveConfirm extends ListenerInvHolder {
    private final Inventory inventory;
    private final String playerName;
    private final String title;
    public RemoveConfirm(String playerName,String title){
        this.inventory = Bukkit.createInventory(this,27,"§cCONFIRM THE DELETION");
        this.playerName = playerName;
        this.title = title;

        {
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(" ");
            itemStack.setItemMeta(itemMeta);
            for (int i = 0; i < 27; i++) {
                this.inventory.setItem(i,itemStack);
            }
        }
        {
            //confirm cancel
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§aCONFIRM");
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(11,itemStack);
            itemStack = itemStack.clone();
            itemStack.setDurability((short) 14);
            itemMeta.setDisplayName("§cCANCEL");
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(15,itemStack);
        }

        this.onClick(e->{
            e.setCancelled(true);
            if (e.getClickedInventory() instanceof PlayerInventory) {
                return;
            }
            int slot = e.getSlot();
            if (slot == 11){
                ArrayList<String> list = Lists.newArrayList(CacheData.playerData.getPlayerTitles(playerName));
                list.remove(this.title);
                CacheData.playerData.setPlayerTitles(playerName,list.toArray(new String[0]));
                String nowPlayerTitle = CacheData.playerData.getNowPlayerTitle(playerName);
                if (nowPlayerTitle.equals(this.title)){
                    CacheData.playerData.setNowPlayerTitle(playerName,null);
                }
                e.getWhoClicked().closeInventory();
                return;
            }
            if (slot == 15){
                e.getWhoClicked().closeInventory();
            }
        });
    }
}
