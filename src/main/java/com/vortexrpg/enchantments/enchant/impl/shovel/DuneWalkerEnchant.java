package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Set;

/** Dune Walker: Mining sand gives temporary speed boost. */
public class DuneWalkerEnchant extends VortexEnchant {
    private static final int[] DURATION = {60, 80, 100};
    private static final Set<Material> SAND = Set.of(Material.SAND, Material.RED_SAND);

    public DuneWalkerEnchant() { super("dune_walker", "Dune Walker", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!SAND.contains(event.getBlock().getType())) return;
        int ticks = cfgi("duration_ticks", DURATION[level - 1]);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 0, true, false));
    }

    @Override public String getDescription() { return "Mining sand gives speed boost."; }
    @Override public String getDescription(int level) {
        return "§7Sand: §aSpeed I§7 for §e" + (DURATION[level - 1] / 20) + "s§7."; }
}
