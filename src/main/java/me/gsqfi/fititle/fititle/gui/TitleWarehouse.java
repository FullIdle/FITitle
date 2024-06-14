package me.gsqfi.fititle.fititle.gui;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.fullidle.ficore.ficore.common.api.ineventory.ListenerInvHolder;
import me.gsqfi.fititle.fititle.data.CacheData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class TitleWarehouse extends ListenerInvHolder {
    private static final ArrayList<Integer> titleSlots = new ArrayList<>();


    private final Inventory inventory;
    private final OfflinePlayer player;
    private final String[] titles;
    private final String nowTitle;
    private int nowPage;
    private final Map<Integer, String> data = new HashMap<>();

    public TitleWarehouse(OfflinePlayer player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54, "§3Title Warehouse");
        String name = player.getName();
        this.titles = CacheData.playerData.getPlayerTitles(name);
        this.nowTitle = CacheData.playerData.getNowPlayerTitle(name);
        {
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(" ");
            itemStack.setItemMeta(itemMeta);
            for (int i = 0; i < 9; i++) {
                this.inventory.setItem(i, itemStack);
            }
            for (int i = 0; i < 5; i++) {
                this.inventory.setItem(i * 9, itemStack);
            }
            for (int i = 1; i < 6; i++) {
                this.inventory.setItem((i * 9) - 1, itemStack);
            }
            for (int i = 45; i < 54; i++) {
                this.inventory.setItem(i,itemStack);
            }
        }
        {
            ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§aYour current title");
            itemMeta.setLore(Arrays.asList(
                    "§3>> §r" + PlaceholderAPI.setPlaceholders(player, this.nowTitle.replace('&', '§')) + " §3<<",
                    "§3Original text of the title↓",
                    this.nowTitle
            ));
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(49, itemStack);
        }
        {
            ItemStack itemStack = new ItemStack(Material.ARROW);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§aPREVIOUS");
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(47, itemStack);
            itemStack = itemStack.clone();
            itemMeta.setDisplayName("§aNEXT");
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(51, itemStack);
        }
        {
            changePage(0);
        }
        eventHandlerInit();
    }

    private void eventHandlerInit() {
        this.onClick(e -> {
            e.setCancelled(true);
            if (e.getClickedInventory() instanceof PlayerInventory) {
                return;
            }
            int slot = e.getSlot();
            if (slot == 47) {
                //
                changePage(this.nowPage - 1);
                return;
            }
            if (slot == 51) {
                //
                changePage(this.nowPage + 1);
                return;
            }

            ClickType click = e.getClick();
            String name = player.getName();
            if (this.data.containsKey(slot)) {
                String title = this.data.get(slot);
                if (click.isRightClick()){
                    //删除
                    RemoveConfirm removeConfirm = new RemoveConfirm(name, title);
                    e.getWhoClicked().openInventory(removeConfirm.getInventory());
                    return;
                }
                CacheData.playerData.setNowPlayerTitle(name, title);
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().sendMessage("§aYour current title is: " + title);
            }
        });
    }

    public boolean changePage(int page) {
        if (page < 0) return false;
        int length = titles.length;
        int start = page * 28;
        int end = start + 28;
        if (start > length - 1) {
            return false;
        }
        for (Integer i : titleSlots) {
            this.inventory.setItem(i,null);
        }
        this.data.clear();
        int j = 0;
        for (int i = start; i < Math.min(end, length); i++) {
            Integer i1 = titleSlots.get(j);
            String title = titles[i];
            this.inventory.setItem(i1,getTitleItemStack(this.player, title));
            this.data.put(i1, title);
            j++;
        }
        this.nowPage = page;
        return true;
    }

    public static ItemStack getTitleItemStack(OfflinePlayer player, String title) {
        ItemStack itemStack = new ItemStack(CacheData.title_warehouse_material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(
                PlaceholderAPI.setPlaceholders(
                        player,
                        CacheData.title_warehouse_name.
                                replace("{title}", title).replace('&','§')));
        itemMeta.setLore(CacheData.title_warehouse_lore
                .stream().map(s -> PlaceholderAPI.setPlaceholders(
                        player, s.replace("{title}", title).replace('&','§')))
                .collect(Collectors.toList()));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    static {
        for (int i = 1; i < 5; i++) {
            for (int j = i * 9 + 1; j < i*9+8; j++) {
                titleSlots.add(j);
            }
        }
    }
}
