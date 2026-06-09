package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Rock Steady: Removes Mining Fatigue when breaking blocks. */
public class RockSteadyEnchant extends VortexEnchant {
    public RockSteadyEnchant() { super("rock_steady", "Rock Steady", EnchantRarity.COMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (player.hasPotionEffect(PotionEffectType.MINING_FATIGUE)) {
            player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
        }
    }

    @Override public String getDescription() { return "Removes Mining Fatigue on block break."; }
    @Override public String getDescription(int level) {
        return "§7Mining: removes §cMining Fatigue§7 on block break."; }
}
