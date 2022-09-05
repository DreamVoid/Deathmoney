package org.mineblock.deathtweak.listener;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineblock.deathtweak.MineBlockDeathTweak;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class EntityDeathListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        FileConfiguration config = MineBlockDeathTweak.INSTANCE.getConfig();

        List<EntityType> listNormal = new ArrayList<>();
        for(String s : config.getStringList("entity.mob-type.normal")){
            try{
                listNormal.add(EntityType.valueOf(s));
            } catch (IllegalArgumentException ignored){}
        }

        List<EntityType> listRare = new ArrayList<>();
        for(String s : config.getStringList("entity.mob-type.rare")){
            try{
                listRare.add(EntityType.valueOf(s));
            } catch (IllegalArgumentException ignored){}
        }

        List<EntityType> listBoss = new ArrayList<>();
        for(String s : config.getStringList("entity.mob-type.boss")){
            try{
                listBoss.add(EntityType.valueOf(s));
            } catch (IllegalArgumentException ignored){}
        }

        EntityType type = event.getEntityType();
        int Money = 0;
        if(listNormal.contains(type)) {
            Money = Utils.getMinMax(config.getInt("min.normal",1), config.getInt("max.normal",2));
        } else if (listRare.contains(type)) {
            Money = Utils.getMinMax(config.getInt("min.rare",10), config.getInt("max.rare",10));
        } else if (listBoss.contains(type)) {
            Money = Utils.getMinMax(config.getInt("min.boss",100),config.getInt("max.boss",500));
        }
        if (Money != 0) {
            ItemStack itemstack = new ItemStack(Material.GOLD_INGOT);
            ItemMeta itemmeta = itemstack.getItemMeta();
            assert itemmeta != null;
            itemmeta.setDisplayName(ChatColor.stripColor("${money}".replace("{money}", Utils.format(Money)).replace(".", ",")));

            ArrayList<String> lore = new ArrayList<>();
            lore.add(0, "MobKillMoney");
            lore.add(1, String.valueOf(Money));
            itemmeta.setLore(lore);
            itemstack.setItemMeta(itemmeta);
            event.getDrops().add(itemstack);
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player) || event.isCancelled()) return;
        Item item = event.getItem();
        Player player = (Player) event.getEntity();
        if (Objects.nonNull(item.getItemStack().getItemMeta()) && item.getItemStack().getItemMeta().getLore() != null) {
            if (!item.getItemStack().getItemMeta().getLore().contains("MobKillMoney")) return;

            ItemStack itemstack = item.getItemStack();
            ItemMeta itemmeta = itemstack.getItemMeta();
            itemmeta.setDisplayName(item.getItemStack().getItemMeta().getDisplayName().replace(".", ",").replace(",", ""));
            itemstack.setItemMeta(itemmeta);
            String string = item.getItemStack().getItemMeta().getDisplayName();
            String money = Utils.getMoney(string);
            MineBlockDeathTweak.economy.depositPlayer(player, Integer.parseInt(money));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 80.0F, 1.0F);
            item.remove();
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        if (item.getItemStack().getItemMeta() != null && item.getItemStack().getItemMeta().getLore() != null) {
            if (!item.getItemStack().getItemMeta().getLore().contains("MobKillMoney")) return;
            item.setCustomName(ChatColor.stripColor(item.getItemStack().getItemMeta().getDisplayName()));
            item.setCustomNameVisible(true);
        }
    }

    @EventHandler
    public void onInventoryPickupByHopper(InventoryPickupItemEvent event) {
        if (event.getInventory().getType().equals(InventoryType.HOPPER) && event.getItem().getItemStack().getItemMeta() != null && event.getItem().getItemStack().getItemMeta().getLore() != null && event.getItem().getItemStack().getItemMeta().getLore().contains("MobKillMoney")) {
            event.setCancelled(true);
        }
    }

    public static class Utils {
        static int getMinMax(int Min, int Max) {
            Random random = new Random();
            return random.nextInt(Max - Min + 1) + Min;
        }

        static String format(int money) {
            DecimalFormat decimalformat = new DecimalFormat("#,###,###");
            return decimalformat.format(money);
        }

        static String getMoney(String a) {
            String[] b = ChatColor.stripColor("${money}").split("\\{money}");
            String c = a;
            byte b1;
            int i;
            String[] arrayOfString1;
            for (i = (arrayOfString1 = b).length, b1 = 0; b1 < i; ) {
                String d = arrayOfString1[b1];
                c = c.replace(d, "");
                b1++;
            }
            return c;
        }
    }
}
