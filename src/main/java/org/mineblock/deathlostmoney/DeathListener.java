package org.mineblock.deathlostmoney;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

import static java.lang.Math.min;

public class DeathListener implements Listener {
    @EventHandler
    public void onEntityDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        double money = main.eco.getBalance(player);
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
            /*if (player.hasPermission("deathmoney.bypass")){
                keep(player, config.getBoolean("ANISHING_CURSE"));
                return;
            }*/

            double lost = min(money * 0.5 >= 0.01 ? money * 0.5 : money, 5000000);
            main.eco.withdrawPlayer(player, lost);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&c你因为死亡损失了" + df.format(lost) + "硬币！"));
            //keep(player, config.getBoolean("ANISHING_CURSE"));
    }

    private void keep(Player player, boolean anishing_curse){
        if (anishing_curse) {
            int j;
            ItemStack itemStack1 = new ItemStack(Material.AIR);
            for (j = 0; j < player.getInventory().getSize(); j++) {
                ItemStack item = player.getInventory().getItem(j);
                if (item != null && item.containsEnchantment(Enchantment.VANISHING_CURSE)) {
                    player.getInventory().setItem(j, itemStack1);
                }
            }
            player.updateInventory();
        }
    }
}
