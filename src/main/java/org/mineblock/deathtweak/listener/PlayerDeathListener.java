package org.mineblock.deathtweak.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.mineblock.deathtweak.MineBlockDeathTweak;

import java.text.DecimalFormat;

public class PlayerDeathListener implements Listener {

	@EventHandler
	public void onEntityDeath(PlayerDeathEvent event) {
		Player player = event.getEntity().getPlayer();

		if(MineBlockDeathTweak.INSTANCE.getConfig().getBoolean("player.lost-money", true)) {
			// 丢钱
			if (MineBlockDeathTweak.economy != null) {
				double money = MineBlockDeathTweak.economy.getBalance(player);
				if (money >= 0.01) {
					DecimalFormat df = new DecimalFormat("#,###,##0.00");
					double lost = Math.min(money * 0.5 >= 0.01 ? money * 0.5 : money, 5000000);
					MineBlockDeathTweak.economy.withdrawPlayer(player, lost);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c你因为死亡损失了" + df.format(lost) + "硬币！"));
				}

				if(player.getLevel() >= 1){
					int lossLevel = player.getLevel() / 2;
					player.setLevel(player.getLevel() - lossLevel);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c你因为死亡损失了" + lossLevel + "级经验！"));
				}
			}
		}

		if(MineBlockDeathTweak.INSTANCE.getConfig().getBoolean("player.lost-vanishing-curse-item", true)) {
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
}
