package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Infusion Pick: Mining specific ores grants brief buffs. */
public class InfusionPickEnchant extends VortexEnchant {
    private static final int[] DURATION = {60, 80, 100};

    public InfusionPickEnchant() { super("infusion_pick", "Infusion", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        int dur = cfgi("duration_ticks", DURATION[level - 1]);
        Material mat = event.getBlock().getType();
        if (mat == Material.IRON_ORE || mat == Material.DEEPSLATE_IRON_ORE) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, dur, 0, true, false));
        } else if (mat == Material.GOLD_ORE || mat == Material.DEEPSLATE_GOLD_ORE) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, dur, 0, true, false));
        } else if (mat == Material.DIAMOND_ORE || mat == Material.DEEPSLATE_DIAMOND_ORE) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, dur, 0, true, false));
        } else if (mat == Material.EMERALD_ORE || mat == Material.DEEPSLATE_EMERALD_ORE) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, dur, 0, true, false));
        }
    }

    @Override public String getDescription() { return "Mining ores grants brief buffs based on ore type."; }
    @Override public String getDescription(int level) {
        return "§7Ore: §bIron§7=Str, §6Gold§7=Speed, §bDiamond§7=Resist, §aEmerald§7=Regen (" + (DURATION[level - 1] / 20) + "s)."; }
}
