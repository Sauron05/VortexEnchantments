package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Lodestone: While in the Nether, passively move 10/15/20% faster. */
public class LodestoneEnchant extends VortexEnchant {
    @SuppressWarnings("unused")
    private static final double[] BONUS = {0.10, 0.15, 0.20};

    public LodestoneEnchant() { super("lodestone", "Lodestone", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (player.getWorld().getEnvironment() != org.bukkit.World.Environment.NETHER) return;
        if (!player.hasPotionEffect(org.bukkit.potion.PotionEffectType.SPEED)) {
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.SPEED, 300, level - 1, true, false, false));
        }
    }

    @Override public String getDescription() { return "Move faster in the Nether."; }
    @Override public String getDescription(int level) {
        return "§7In Nether: §aSpeed " + level + "§7 passive."; }
}
