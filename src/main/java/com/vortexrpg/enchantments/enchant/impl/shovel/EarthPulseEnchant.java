package com.vortexrpg.enchantments.enchant.impl.shovel;

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

/** Earth Pulse: Breaking ground slows nearby mobs. */
public class EarthPulseEnchant extends VortexEnchant {
    private static final double[] RADIUS = {4, 5, 6};

    public EarthPulseEnchant() { super("earth_pulse", "Earth Pulse", EnchantRarity.RARE, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double radius = cfg("radius", RADIUS[level - 1]);
        for (LivingEntity e : MathUtil.getNearbyLiving(event.getBlock().getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, cfgi("slow_ticks", 40), 1, true, false));
        }
        SoundUtil.play(event.getBlock().getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 0.5f, 0.5f);
    }

    @Override public String getDescription() { return "Breaking ground slows nearby mobs."; }
    @Override public String getDescription(int level) {
        return "§7Dig: §cSlowness II§7 to mobs within §a" + (int) RADIUS[level - 1] + " blocks§7."; }
}
