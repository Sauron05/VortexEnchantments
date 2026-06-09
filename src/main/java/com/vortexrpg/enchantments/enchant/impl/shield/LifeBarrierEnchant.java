package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** Life Barrier: Below 30% HP, massive damage reduction while blocking. */
public class LifeBarrierEnchant extends VortexEnchant {

    public LifeBarrierEnchant() { super("life_barrier", "Life Barrier", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double threshold = cfg("hp-threshold", 0.3);
        if (player.getHealth() / maxHp > threshold) return;
        double reduction = cfg("reduction", 0.3 + level * 0.1);
        event.setDamage(event.getDamage() * (1.0 - reduction));
    }

    @Override public String getDescription() { return "Below 30% HP: massive block reduction."; }
    @Override public String getDescription(int level) {
        return "§7Block below 30% HP: §a" + (int)((0.3 + level * 0.1) * 100) + "%§7 extra reduction."; }
}
