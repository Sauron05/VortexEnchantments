package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import org.bukkit.event.Listener;

public class FarmingListener implements Listener {

    @SuppressWarnings("unused") // Reserved for future farming hooks
    private final VortexEnchantments plugin;

    public FarmingListener(VortexEnchantments plugin) {
        this.plugin = plugin;
    }
    // Additional farming hooks beyond EnchantListener's onHarvest are added here as needed
}
