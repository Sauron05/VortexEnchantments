package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** Shock Absorb: Blocking explosion damage heavily reduced. */
public class ShockAbsorbEnchant extends VortexEnchant {

    public ShockAbsorbEnchant() { super("shock_absorb", "Shock Absorb", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                && event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) return;
        double reduction = cfg("reduction", 0.3 + level * 0.15);
        event.setDamage(event.getDamage() * (1.0 - reduction));
    }

    @Override public String getDescription() { return "Blocking reduces explosion damage."; }
    @Override public String getDescription(int level) {
        return "§7Block explosions: §a" + (int)((0.3 + level * 0.15) * 100) + "%§7 damage reduction."; }
}
