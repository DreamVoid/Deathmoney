package org.mineblock.deathlost;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

import static java.lang.Math.min;


public class main extends JavaPlugin implements Listener {
    public static Economy eco = null;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        RegisteredServiceProvider<Economy> economyP = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyP != null) {
            eco = economyP.getProvider();
        } else {
            getLogger().info( "Unable to initialize Economy Interface with Vault!");
        }
    }

    @EventHandler
    public void onEntityDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();

        // 丢钱
        double money = main.eco.getBalance(player);
        if(money >= 0.01){
            DecimalFormat df = new DecimalFormat("#,###,##0.00");
            double lost = min(money * 0.5 >= 0.01 ? money * 0.5 : money, 5000000);
            main.eco.withdrawPlayer(player, lost);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&c你因为死亡损失了" + df.format(lost) + "硬币！"));
        }

        // 丢消失诅咒装备
        ItemStack air = new ItemStack(Material.AIR);
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.containsEnchantment(Enchantment.VANISHING_CURSE)) {
                player.getInventory().setItem(i, air);
            }
        }
        player.updateInventory();
    }
}