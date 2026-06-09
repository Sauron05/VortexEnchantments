package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Deflect: 8/12/16% chance to reduce projectile damage by 80%. */
public class DeflectEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.08, 0.12, 0.16};

    public DeflectEnchant() { super("deflect", "Deflect", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof org.bukkit.entity.Projectile)) return;
        double chance = cfg("chance", CHANCE[level-1]);
        if (Math.random() < chance) {
            event.setDamage(event.getDamage() * 0.20);
        }
    }

    @Override public String getDescription() { return "Chance to deflect projectile hits."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level-1]*100) + "§a%§7 to reduce projectile damage by §a80§a%§7."; }
}
