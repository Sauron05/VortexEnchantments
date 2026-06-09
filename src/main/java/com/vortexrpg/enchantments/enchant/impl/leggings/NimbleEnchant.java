package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Nimble: Passive Speed I/II/III. */
public class NimbleEnchant extends VortexEnchant {
    public NimbleEnchant() { super("nimble", "Nimble", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, level - 1, true, false, false));
        }
    }

    @Override public String getDescription() { return "Grants passive Speed."; }
    @Override public String getDescription(int level) {
        return "§7Passive §aSpeed " + level + "§7 while worn."; }
}
