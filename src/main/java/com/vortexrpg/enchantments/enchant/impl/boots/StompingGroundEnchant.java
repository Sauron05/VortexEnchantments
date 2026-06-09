package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * StompingGround: Fall damage knockbacks and slows nearby enemies.
 */
public class StompingGroundEnchant extends VortexEnchant {
    public StompingGroundEnchant() { super("stomping_ground", "Stomping Ground", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        double radius = cfgd("radius", 4.0 + level);
        int dur = cfgi("slow_duration", 30 + level * 10);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, dur, level - 1, true, false, true));
            org.bukkit.util.Vector push = e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(0.5 * level);
            e.setVelocity(push.setY(0.3));
        }
        SoundUtil.play(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 0.7f, 0.5f);
    }

    @Override public String getDescription(int level) {
        return "§7Fall landings slow and knockback nearby enemies.";
    }
}
