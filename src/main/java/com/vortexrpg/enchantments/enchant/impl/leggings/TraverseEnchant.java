package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Traverse: Auto-step up 1.5/2/2.5 block heights (like a step enhancement). */
@SuppressWarnings("deprecation")
public class TraverseEnchant extends VortexEnchant {
    @SuppressWarnings("unused")
    private static final float[] STEP = {1.5f, 2.0f, 2.5f};

    public TraverseEnchant() { super("traverse", "Traverse", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        // Adjust walk speed slightly to represent ease of traversal
        // Actual step height requires NMS; approximate with jump boost when moving
        if (player.isOnGround() && player.getVelocity().lengthSquared() > 0.01) {
            if (!player.hasPotionEffect(org.bukkit.potion.PotionEffectType.JUMP_BOOST)) {
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.JUMP_BOOST, 20, level - 1, true, false, false));
            }
        }
    }

    @Override public String getDescription() { return "Provides gentle Jump Boost while moving."; }
    @Override public String getDescription(int level) {
        return "§7Moving: §aJump Boost " + level + "§7 (passive)."; }
}
