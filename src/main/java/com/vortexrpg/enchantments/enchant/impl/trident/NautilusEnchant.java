package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Nautilus: Returning trident (Loyalty) heals 8/10/12% of damage dealt. */
@SuppressWarnings("deprecation")
public class NautilusEnchant extends VortexEnchant {
    private static final double[] HEAL_PERCENT = {0.08, 0.10, 0.12};

    public NautilusEnchant() { super("nautilus", "Nautilus", EnchantRarity.RARE, 3, List.of(ItemTarget.TRIDENT)); }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        if (!isEnabled()) return;
        double heal = event.getFinalDamage() * cfg("heal_percent", HEAL_PERCENT[level-1]);
        double newHp = Math.min(thrower.getMaxHealth(), thrower.getHealth() + heal);
        thrower.setHealth(newHp);
    }

    @Override public String getDescription() { return "Trident return heals based on damage dealt."; }
    @Override public String getDescription(int level) {
        return "§7Trident hit heals you §a" + (int)(HEAL_PERCENT[level-1]*100) + "%§7 of damage dealt."; }
}
