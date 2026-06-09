package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Mariner: Holding trident gives Dolphins Grace. Thrown in water: 2×/2.5×/3× speed/damage.
 */
public class MarinerEnchant extends VortexEnchant {
    private static final double[] WATER_MULT = {2.0, 2.5, 3.0};

    public MarinerEnchant() { super("mariner", "Mariner", EnchantRarity.RARE, 3, List.of(ItemTarget.TRIDENT)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 40, 0, false, false, false));
    }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (!thrower.isInWater() && !target.isInWater()) return;
        event.setDamage(event.getDamage() * WATER_MULT[level-1]);
    }

    @Override public String getDescription() { return "Passive Dolphin's Grace. Aquatic throws deal bonus damage."; }
    @Override public String getDescription(int level) {
        return "§7Holding: §bDolphin's Grace§7 passive. Throws in water: §c" + WATER_MULT[level-1] + "×§7 damage."; }
}
