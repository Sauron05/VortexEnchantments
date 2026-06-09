package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * ImpactCrater: Landing from a fall deals AOE damage.
 */
public class ImpactCraterEnchant extends VortexEnchant {
    public ImpactCraterEnchant() { super("impact_crater", "Impact Crater", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        double radius = cfgd("radius", 3.0 + level);
        double dmg = cfgd("aoe_damage", event.getFinalDamage() * (0.3 + level * 0.15));
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.damage(dmg, player);
        }
        ParticleUtil.drawCircle(player.getLocation().add(0, 0.1, 0), radius, 20, Particle.CAMPFIRE_COSY_SMOKE);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 0.8f, 0.6f);
    }

    @Override public String getDescription(int level) {
        return "§7Fall landings deal §c" + (30 + level * 15) + "% §7of fall damage as AOE.";
    }
}
