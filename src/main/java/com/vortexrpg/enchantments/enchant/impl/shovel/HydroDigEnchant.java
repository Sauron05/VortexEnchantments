package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Hydro Dig: Haste while standing in water. */
public class HydroDigEnchant extends VortexEnchant {
    public HydroDigEnchant() { super("hydro_dig", "Hydro Dig", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (player.isInWater()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 40, level - 1, true, false));
        }
    }

    @Override public String getDescription() { return "Mining speed boost while in water."; }
    @Override public String getDescription(int level) {
        return "§7In water: §aHaste " + level + "§7."; }
}
