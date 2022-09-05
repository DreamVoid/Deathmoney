package org.mineblock.deathtweak;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mineblock.deathtweak.listener.EntityDeathListener;
import org.mineblock.deathtweak.listener.PlayerDeathListener;


public class MineBlockDeathTweak extends JavaPlugin {
    public static MineBlockDeathTweak INSTANCE;
    public static Economy economy = null;

    @Override
    public void onLoad() {
        INSTANCE = this;
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> economyP = getServer().getServicesManager().getRegistration(Economy.class);
            if (economyP != null) {
                economy = economyP.getProvider();
                getLogger().info("Vault registered.");
            } else {
                getLogger().info("Unable to initialize Economy Interface with Vault!");
            }
        }

        if(getConfig().getBoolean("player.lost-money", true) || getConfig().getBoolean("player.lost-vanishing-curse-item", true)) {
            getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        }

        if(getConfig().getBoolean("entity.mob-drop-money", true)){
            getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);
        }
    }
}