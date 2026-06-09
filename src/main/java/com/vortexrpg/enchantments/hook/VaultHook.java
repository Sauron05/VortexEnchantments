package com.vortexrpg.enchantments.hook;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Optional Vault economy integration.
 */
public class VaultHook {

    private Economy economy;
    private boolean enabled;

    public void setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            enabled = false;
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
            enabled = true;
        }
    }

    public boolean isEnabled() { return enabled && economy != null; }

    public double getBalance(Player player) {
        return enabled ? economy.getBalance(player) : 0;
    }

    public boolean has(Player player, double amount) {
        return enabled && economy.has(player, amount);
    }

    public boolean withdraw(Player player, double amount) {
        if (!enabled) return false;
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public boolean deposit(Player player, double amount) {
        if (!enabled) return false;
        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    public String format(double amount) {
        return enabled ? economy.format(amount) : String.format("$%.2f", amount);
    }
}
