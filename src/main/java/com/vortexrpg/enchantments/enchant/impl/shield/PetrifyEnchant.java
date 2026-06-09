package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Petrify: Blocking gaze slows enemies facing you. */
public class PetrifyEnchant extends VortexEnchant {

    public PetrifyEnchant() { super("petrify", "Petrify", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 20 != 0) return;
        double radius = cfg("radius", 5.0 + level);
        int duration = cfgi("slow-duration", 20 + level * 10);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            var toPlayer = player.getLocation().toVector().subtract(e.getLocation().toVector()).normalize();
            if (e.getLocation().getDirection().angle(toPlayer) < Math.PI / 4) {
                e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, level - 1));
            }
        }
    }

    @Override public String getDescription() { return "Enemies facing you while blocking are slowed."; }
    @Override public String getDescription(int level) {
        return "§7Block: enemies facing you get §9Slowness " + level + "§7 in §e" + (int)(5 + level) + "b§7."; }
}
