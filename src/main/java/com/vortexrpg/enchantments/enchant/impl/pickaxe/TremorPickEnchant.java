package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Tremor Pick: Mining sends shockwave that slows nearby mobs. */
public class TremorPickEnchant extends VortexEnchant {
    private static final double[] RADIUS = {3, 4, 5};

    public TremorPickEnchant() { super("tremor_pick", "Tremor", EnchantRarity.EPIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!MathUtil.chance(cfg("chance", 20))) return;
        double radius = cfg("radius", RADIUS[level - 1]);
        int duration = cfgi("slow_ticks", 40);
        for (LivingEntity e : MathUtil.getNearbyLiving(event.getBlock().getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 1, true, false));
        }
        SoundUtil.play(event.getBlock().getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 0.5f, 0.6f);
    }

    @Override public String getDescription() { return "Mining may slow nearby mobs."; }
    @Override public String getDescription(int level) {
        return "§7Mining: §cSlowness II§7 to mobs within §a" + (int) RADIUS[level - 1] + " blocks§7."; }
}
