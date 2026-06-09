package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Sunrise: During daytime, passive Haste while holding hoe. */
public class SunriseEnchant extends VortexEnchant {

    public SunriseEnchant() { super("sunrise", "Sunrise", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long time = player.getWorld().getTime();
        if (time >= 0 && time < 12000) {
            if (!player.hasPotionEffect(PotionEffectType.HASTE)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 40, level - 1, true, false, false));
            }
        }
    }

    @Override public String getDescription() { return "Daytime Haste while holding hoe."; }
    @Override public String getDescription(int level) {
        return "§7Daytime: §bHaste " + level + "§7 while holding."; }
}
