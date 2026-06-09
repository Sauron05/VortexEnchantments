package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Tempest: During storms auto-channel lightning + chain to 1/2/3 nearby mobs. */
public class TempestEnchant extends VortexEnchant {
    private static final int[] CHAIN_COUNT = {1, 2, 3};

    public TempestEnchant() { super("tempest", "Tempest", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.TRIDENT)); }

    private void apply(Player attacker, LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (!attacker.getWorld().isThundering()) return;
        double chainRadius = cfg("chain_radius", 6.0);
        int chainCount = cfgi("chain_count_" + level, CHAIN_COUNT[level-1]);
        target.getWorld().strikeLightningEffect(target.getLocation());
        int chains = 0;
        for (LivingEntity nearby : MathUtil.getNearbyLiving(target.getLocation(), chainRadius)) {
            if (nearby.equals(attacker) || nearby.equals(target)) continue;
            if (chains >= chainCount) break;
            nearby.getWorld().strikeLightningEffect(nearby.getLocation());
            nearby.damage(4.0, attacker);
            chains++;
        }
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        apply(attacker, target, level);
    }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        apply(thrower, target, level);
    }

    @Override public String getDescription() { return "Channels lightning in storms."; }
    @Override public String getDescription(int level) {
        return "§7Storms: §elightning chain§7 to §a" + CHAIN_COUNT[level-1] + " nearby§7 mobs."; }
}
