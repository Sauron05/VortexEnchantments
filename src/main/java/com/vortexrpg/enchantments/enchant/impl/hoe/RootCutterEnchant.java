package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Root Cutter: Breaking root/vine blocks gives brief Haste. */
public class RootCutterEnchant extends VortexEnchant {

    public RootCutterEnchant() { super("root_cutter", "Root Cutter", EnchantRarity.COMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.VINE && mat != Material.CAVE_VINES && mat != Material.CAVE_VINES_PLANT
                && mat != Material.HANGING_ROOTS && mat != Material.ROOTED_DIRT
                && mat != Material.MANGROVE_ROOTS && mat != Material.MUDDY_MANGROVE_ROOTS) return;
        int duration = cfgi("haste-duration", 40 + level * 20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, duration, level - 1));
    }

    @Override public String getDescription() { return "Breaking roots/vines gives Haste."; }
    @Override public String getDescription(int level) {
        return "§7Break roots/vines: §bHaste " + level + "§7 for " + (2 + level) + "s."; }
}
