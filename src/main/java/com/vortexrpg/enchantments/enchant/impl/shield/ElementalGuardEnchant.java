package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** Elemental Guard: Reduce fire/frost/lightning damage while blocking. */
public class ElementalGuardEnchant extends VortexEnchant {

    public ElementalGuardEnchant() { super("elemental_guard", "Elemental Guard", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        var cause = event.getCause();
        if (cause != EntityDamageEvent.DamageCause.FIRE
                && cause != EntityDamageEvent.DamageCause.FIRE_TICK
                && cause != EntityDamageEvent.DamageCause.LAVA
                && cause != EntityDamageEvent.DamageCause.FREEZE
                && cause != EntityDamageEvent.DamageCause.LIGHTNING) return;
        double reduction = cfg("reduction", 0.2 + level * 0.1);
        event.setDamage(event.getDamage() * (1.0 - reduction));
    }

    @Override public String getDescription() { return "Reduce elemental damage while blocking."; }
    @Override public String getDescription(int level) {
        return "§7Block: §a" + (int)((0.2 + level * 0.1) * 100) + "%§7 less fire/freeze/lightning."; }
}
