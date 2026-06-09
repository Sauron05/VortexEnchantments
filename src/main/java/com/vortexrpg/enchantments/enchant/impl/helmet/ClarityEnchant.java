package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Clarity: Passive Haste while worn; higher levels increase amplifier. */
public class ClarityEnchant extends VortexEnchant {
    public ClarityEnchant() { super("clarity", "Clarity", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.hasPotionEffect(PotionEffectType.HASTE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 300, level - 1, true, false, false));
        }
    }

    @Override public String getDescription() { return "Grants passive Haste while worn."; }
    @Override public String getDescription(int level) {
        return "§7Passive §aHaste " + level + "§7 while worn."; }
}
