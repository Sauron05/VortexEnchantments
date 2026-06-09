package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** Bulwark: Blocking fire damage reduces fire ticks. */
public class BulwarkEnchant extends VortexEnchant {

    public BulwarkEnchant() { super("bulwark", "Bulwark", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FIRE
                && event.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK
                && event.getCause() != EntityDamageEvent.DamageCause.LAVA) return;
        double reduction = cfg("fire-reduction", 0.3 + level * 0.15);
        event.setDamage(event.getDamage() * (1.0 - reduction));
        int newTicks = (int)(player.getFireTicks() * (1.0 - reduction));
        player.setFireTicks(Math.max(0, newTicks));
    }

    @Override public String getDescription() { return "Blocking reduces fire damage & ticks."; }
    @Override public String getDescription(int level) {
        return "§7Block fire: §a" + (int)((0.3 + level * 0.15) * 100) + "%§7 less fire damage/ticks."; }
}
