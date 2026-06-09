package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** Steady Guard: Small damage reduction while blocking. */
public class SteadyGuardEnchant extends VortexEnchant {

    public SteadyGuardEnchant() { super("steady_guard", "Steady Guard", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        double reduction = cfg("reduction", 0.05 * level);
        event.setDamage(event.getDamage() * (1.0 - reduction));
    }

    @Override public String getDescription() { return "Small damage reduction while blocking."; }
    @Override public String getDescription(int level) {
        return "§7Block: §a" + (int)(5 * level) + "%§7 extra damage reduction."; }
}
