package org.mineblock.deathlostmoney;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class main extends JavaPlugin implements CommandExecutor{
    public static Economy eco = null;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new DeathListener(), this);

        RegisteredServiceProvider<Economy> economyP = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyP != null) {
            eco = economyP.getProvider();
        } else {
            getLogger().info( "Unable to initialize Economy Interface with Vault!");
        }
    }
}