package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

import java.util.List;

/** Life Weaver: Crops in radius give XP and heal on growth ticks. */
public class LifeWeaverEnchant extends VortexEnchant {

    public LifeWeaverEnchant() { super("life_weaver", "Life Weaver", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 100 != 0) return;
        int radius = cfgi("radius", 5 + level * 2);
        Block center = player.getLocation().getBlock();
        int growingCount = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block b = center.getRelative(x, 0, z);
                if (b.getBlockData() instanceof Ageable age) {
                    if (age.getAge() > 0 && age.getAge() < age.getMaximumAge()) {
                        growingCount++;
                    }
                }
            }
        }
        if (growingCount > 0) {
            int xp = cfgi("xp-per-crop", 1) * Math.min(growingCount, 10 * level);
            player.giveExp(xp);
            double heal = cfg("heal", 0.1 * level) * Math.min(growingCount, 10);
            double maxHealth = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
            player.setHealth(Math.min(maxHealth, player.getHealth() + heal));
        }
    }

    @Override public String getDescription() { return "Growing crops give XP and heal you."; }
    @Override public String getDescription(int level) {
        return "§7Passive: growing crops in §e" + (5 + level * 2) + "§7b give §aXP§7 + §chealth§7."; }
}
