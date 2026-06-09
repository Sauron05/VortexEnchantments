package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** Spin Block: Blocking protects from all directions (360-degree block). */
public class SpinBlockEnchant extends VortexEnchant {

    public SpinBlockEnchant() { super("spin_block", "Spin Block", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        // Shield normally only blocks frontal; this reduces back/side damage too
        double reduction = cfg("reduction", 0.15 + level * 0.1);
        event.setDamage(event.getDamage() * (1.0 - reduction));
    }

    @Override public String getDescription() { return "Block protects from all directions."; }
    @Override public String getDescription(int level) {
        return "§7Block: §a" + (int)((0.15 + level * 0.1) * 100) + "%§7 reduction from all directions."; }
}
