package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** PoseidonsGift: Water Breathing + Dolphins Grace while holding rod near water. */
public class PoseidonsGiftEnchant extends VortexEnchant {

    public PoseidonsGiftEnchant() { super("poseidons_gift", "Poseidon's Gift", EnchantRarity.EPIC, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        boolean nearWater = false;
        for (int x = -2; x <= 2 && !nearWater; x++) {
            for (int y = -2; y <= 2 && !nearWater; y++) {
                for (int z = -2; z <= 2 && !nearWater; z++) {
                    if (player.getLocation().getBlock().getRelative(x, y, z).getType() == Material.WATER) {
                        nearWater = true;
                    }
                }
            }
        }
        if (nearWater) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 60, 0, true, false, true));
            if (level >= 2) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 60, 0, true, false, true));
            }
        }
    }

    @Override public String getDescription() { return "Water buffs while near water with rod."; }
    @Override public String getDescription(int level) {
        return "§7Near water: §aWater Breathing§7" + (level >= 2 ? " + §bDolphins Grace§7" : "") + "."; }
}
