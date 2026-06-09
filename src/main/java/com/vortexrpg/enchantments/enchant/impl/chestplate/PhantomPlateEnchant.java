package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** PhantomPlate: 20/25/30% chance to phase through hit (cancel damage; visual only). */
public class PhantomPlateEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.20, 0.25, 0.30};

    public PhantomPlateEnchant() { super("phantom_plate", "Phantom Plate", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double chance = cfg("chance", CHANCE[level-1]);
        if (Math.random() < chance) {
            event.setCancelled(true);
            com.vortexrpg.enchantments.util.ParticleUtil.burst(player.getLocation(), org.bukkit.Particle.PORTAL, 20, 0.5f);
        }
    }

    @Override public String getDescription() { return "Chance to phase through hits."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level-1]*100) + "§a%§7 chance to phase through damage."; }
}
