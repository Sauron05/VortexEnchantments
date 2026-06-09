package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Exoskeleton: Reduce 5/8/12% of all physical damage passively. */
public class ExoskeletonEnchant extends VortexEnchant {
    private static final double[] REDUCE = {0.05, 0.08, 0.12};

    public ExoskeletonEnchant() { super("exoskeleton", "Exoskeleton", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double reduce = cfg("reduce", REDUCE[level-1]);
        event.setDamage(event.getDamage() * (1.0 - reduce));
    }

    @Override public String getDescription() { return "Passively absorbs a portion of all damage."; }
    @Override public String getDescription(int level) {
        return "§7Passive §a-" + (int)(REDUCE[level-1]*100) + "§a%§7 damage reduction."; }
}
