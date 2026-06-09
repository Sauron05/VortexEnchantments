package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Infinity Plate: Absorb lethal blow, explode, revive with full HP. Very long CD.
 */
public class InfinityPlateEnchant extends VortexEnchant {
    public InfinityPlateEnchant() { super("infinity_plate", "Infinity Plate", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(victim)) return;
        if (victim.getHealth() - event.getFinalDamage() > 0) return;

        event.setCancelled(true);
        double maxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        victim.setHealth(maxHp);

        double radius = cfgd("explosion_radius", 6.0 + level * 2.0);
        double dmg = cfgd("explosion_damage", 6.0 + level * 4.0);

        for (LivingEntity e : MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (e.equals(victim)) continue;
            e.damage(dmg, victim);
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1, false, false, false));
        }

        ParticleUtil.burst(victim.getLocation(), Particle.TOTEM_OF_UNDYING, 50, 3.0);
        ParticleUtil.burst(victim.getLocation(), Particle.EXPLOSION, 5, 2.0);
        SoundUtil.play(victim.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 0.8f);
        setCooldownFromConfig(victim, "cooldown", 180.0);
    }

    @Override public String getDescription(int level) {
        return "§7Lethal hit: §6§lREVIVE §7at full HP + §cexplode§7. §8180s CD.";
    }
}
