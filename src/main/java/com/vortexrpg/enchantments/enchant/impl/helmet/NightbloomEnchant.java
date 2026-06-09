package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Nightbloom: At nighttime, gain passive regeneration. */
public class NightbloomEnchant extends VortexEnchant {
    public NightbloomEnchant() { super("nightbloom", "Nightbloom", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long time = player.getWorld().getTime();
        if (time < 13000 || time > 23000) return;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        if (player.getHealth() < maxHp) {
            double heal = cfgd("heal_per_tick", 0.25 * level);
            player.setHealth(Math.min(maxHp, player.getHealth() + heal));
        }
    }

    @Override public String getDescription(int level) {
        return "§7At night, passively regenerate §a" + (0.25 * level) + "§7 HP/s.";
    }
}
