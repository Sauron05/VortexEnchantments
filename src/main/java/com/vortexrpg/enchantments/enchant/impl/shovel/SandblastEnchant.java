package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Set;

/** Sandblast: Mining sand blinds nearby mobs. */
public class SandblastEnchant extends VortexEnchant {
    private static final int[] BLIND_TICKS = {20, 30, 40};
    private static final Set<Material> SAND = Set.of(Material.SAND, Material.RED_SAND);

    public SandblastEnchant() { super("sandblast", "Sandblast", EnchantRarity.RARE, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!SAND.contains(event.getBlock().getType())) return;
        double radius = cfg("radius", 4.0);
        int ticks = cfgi("blind_ticks", BLIND_TICKS[level - 1]);
        for (LivingEntity e : MathUtil.getNearbyLiving(event.getBlock().getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ticks, 0, true, false));
        }
    }

    @Override public String getDescription() { return "Mining sand blinds nearby mobs."; }
    @Override public String getDescription(int level) {
        return "§7Sand: §8Blindness§7 to mobs for §a" + (BLIND_TICKS[level - 1] / 20.0) + "s§7."; }
}
