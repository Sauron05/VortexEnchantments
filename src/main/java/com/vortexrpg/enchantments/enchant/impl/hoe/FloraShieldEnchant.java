package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Flora Shield: Harvesting crops grants brief Resistance. */
public class FloraShieldEnchant extends VortexEnchant {

    public FloraShieldEnchant() { super("flora_shield", "Flora Shield", EnchantRarity.EPIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getBlock().getBlockData() instanceof Ageable age)) return;
        if (age.getAge() < age.getMaximumAge()) return;
        int duration = cfgi("duration", 40 + level * 20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, level - 1));
    }

    @Override public String getDescription() { return "Harvesting crops grants Resistance."; }
    @Override public String getDescription(int level) {
        return "§7Harvest: §9Resistance " + level + "§7 for " + (2 + level) + "s."; }
}
