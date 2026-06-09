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

/** Frost Dig: Mining snow/ice grants brief resistance and speed. */
public class FrostDigEnchant extends VortexEnchant {
    private static final int[] DURATION = {60, 80, 100};
    private static final Set<Material> FROST = Set.of(
            Material.SNOW_BLOCK, Material.SNOW, Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE);

    public FrostDigEnchant() { super("frost_dig", "Frost Dig", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!FROST.contains(event.getBlock().getType())) return;
        int ticks = cfgi("duration_ticks", DURATION[level - 1]);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 0, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 0, true, false));
    }

    @Override public String getDescription() { return "Mining frost blocks grants resistance and speed."; }
    @Override public String getDescription(int level) {
        return "§7Snow/Ice: §bResistance I§7 + §aSpeed I§7 for " + (DURATION[level - 1] / 20) + "s."; }
}
