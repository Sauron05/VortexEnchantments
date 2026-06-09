package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Apotheosis: Upon taking lethal damage, avoid death, full heal, and explode with a damage burst.
 */
public class ApotheosisEnchant extends VortexEnchant {
    public ApotheosisEnchant() { super("apotheosis", "Apotheosis", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double hpAfter = player.getHealth() - event.getFinalDamage();
        if (hpAfter >= 0) return;
        if (isOnCooldown(player)) return;

        event.setDamage(0);
        double maxHp = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        double healPct = cfgd("heal_pct", 0.30 + level * 0.15);
        player.setHealth(maxHp * healPct);

        double radius = cfgd("burst_radius", 6.0);
        double burstDmg = cfgd("burst_damage", 4.0 * level);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.damage(burstDmg, player);
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1, true, false, true));
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60 + level * 20, 1, true, false, true));
        ParticleUtil.burst(player.getLocation().add(0, 1, 0), Particle.TOTEM_OF_UNDYING, 50, radius);
        ParticleUtil.burst(player.getLocation().add(0, 1, 0), Particle.FLAME, 30, radius);
        SoundUtil.play(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 0.6f);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.2f);
        setCooldownFromConfig(player, "cooldown", 300.0);
    }

    @Override public String getDescription(int level) {
        return "§7Avoid death: §6heal + AOE burst §7(" + (4 * level) + " dmg). §8300s CD.";
    }
}
