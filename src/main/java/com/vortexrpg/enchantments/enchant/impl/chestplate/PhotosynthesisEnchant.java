package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Photosynthesis: In direct sunlight, slowly regenerate health (Regen I/II/III). */
public class PhotosynthesisEnchant extends VortexEnchant {
    public PhotosynthesisEnchant() { super("photosynthesis", "Photosynthesis", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.getWorld().isDayTime()) return;
        if (player.getWorld().getHighestBlockAt(player.getLocation()).getY() > player.getLocation().getY()) return;
        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, level - 1, true, false, false));
        }
    }

    @Override public String getDescription() { return "Sunlight gradually heals you."; }
    @Override public String getDescription(int level) {
        return "§7In daylight: §aRegen " + level + "§7 passive."; }
}
