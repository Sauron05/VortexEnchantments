package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Deep Dive: Grants Haste when mining below Y=32. */
public class DeepDiveEnchant extends VortexEnchant {
    public DeepDiveEnchant() { super("deep_dive", "Deep Dive", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (player.getLocation().getBlockY() < cfgi("threshold_y", 32)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 40, level - 1, true, false));
        }
    }

    @Override public String getDescription() { return "Mining speed boost below Y=32."; }
    @Override public String getDescription(int level) {
        return "§7Below Y=32: §aHaste " + level + "§7."; }
}
