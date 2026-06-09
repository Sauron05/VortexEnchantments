package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Paradox: On death (before it fires), give Speed + Strength briefly in a last stand. */
public class ParadoxEnchant extends VortexEnchant {
    public ParadoxEnchant() { super("paradox", "Paradox", EnchantRarity.EPIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onRespawn(Player player, int level) {
        if (!isEnabled()) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * (3 + level), level - 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * (3 + level), level - 1));
    }

    @Override public String getDescription() { return "Respawn with a burst of Speed and Strength."; }
    @Override public String getDescription(int level) {
        return "§7Respawn with §aSpeed " + level + "§7 + §aStrength " + level + "§7 for " + (3+level) + "s."; }
}
