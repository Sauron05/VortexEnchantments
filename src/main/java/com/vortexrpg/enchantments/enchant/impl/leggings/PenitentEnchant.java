package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Penitent: Damage scales inversely with food bar: fuller = more damage. Up to +10/15/20%. */
public class PenitentEnchant extends VortexEnchant {
    private static final double[] MAX_BONUS = {0.10, 0.15, 0.20};

    public PenitentEnchant() { super("penitent", "Penitent", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity target, int level) {
        if (!isEnabled()) return;
        double foodFraction = player.getFoodLevel() / 20.0;
        double bonus = foodFraction * cfg("max_bonus", MAX_BONUS[level-1]);
        event.setDamage(event.getDamage() * (1.0 + bonus));
    }

    @Override public String getDescription() { return "Well-fed grants bonus damage."; }
    @Override public String getDescription(int level) {
        return "§7Full hunger: §a+" + (int)(MAX_BONUS[level-1]*100) + "§a%§7 damage (scales with hunger)."; }
}
