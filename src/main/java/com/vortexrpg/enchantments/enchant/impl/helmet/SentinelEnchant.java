package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Sentinel: While standing still, damage taken reduced by 10/15/20%. */
public class SentinelEnchant extends VortexEnchant {
    private static final double[] REDUCE = {0.10, 0.15, 0.20};

    public SentinelEnchant() { super("sentinel", "Sentinel", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (player.getVelocity().lengthSquared() > 0.01) return;
        double reduce = cfg("reduce", REDUCE[level-1]);
        event.setDamage(event.getDamage() * (1.0 - reduce));
    }

    @Override public String getDescription() { return "Standing still reduces damage taken."; }
    @Override public String getDescription(int level) {
        return "§7Standing still: §a-" + (int)(REDUCE[level-1]*100) + "§a%§7 damage."; }
}
