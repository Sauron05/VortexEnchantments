package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Diadem: Passive Luck I/II/III while wearing. */
public class DiademEnchant extends VortexEnchant {
    public DiademEnchant() { super("diadem", "Diadem", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.hasPotionEffect(PotionEffectType.LUCK)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 300, level - 1, true, false, false));
        }
    }

    @Override public String getDescription() { return "Grants passive Luck while worn."; }
    @Override public String getDescription(int level) {
        return "§7Passive §aLuck " + level + "§7 while worn."; }
}
