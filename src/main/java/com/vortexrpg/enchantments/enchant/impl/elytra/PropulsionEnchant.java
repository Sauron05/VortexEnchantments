package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/** Propulsion: Periodic forward velocity boost every few seconds while gliding. */
public class PropulsionEnchant extends VortexEnchant {

    public PropulsionEnchant() { super("propulsion", "Propulsion", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        int interval = cfgi("interval", Math.max(1, 4 - level));
        int ticks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "propulsion_t", 0) + 1;
        if (ticks >= interval) {
            ticks = 0;
            double boost = cfgd("boost", 0.15 + level * 0.05);
            Vector dir = player.getLocation().getDirection().multiply(boost);
            player.setVelocity(player.getVelocity().add(dir));
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "propulsion_t", ticks);
    }

    @Override public String getDescription() { return "Periodic thrust while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Forward boost every §e" + Math.max(1, 4 - level) + "s§7 while gliding."; }
}
