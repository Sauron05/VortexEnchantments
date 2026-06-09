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
 * Apotheosis: When below 20% HP and hit, transform into god form —
 * gain all buffs, AoE damage pulse, massive resistance. 90s CD.
 */
public class ApotheosisEnchant extends VortexEnchant {
    public ApotheosisEnchant() { super("apotheosis", "Apotheosis", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(victim)) return;
        double maxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double threshold = cfgd("hp_threshold", 0.20);
        if (victim.getHealth() / maxHp > threshold) return;

        int dur = cfgi("buff_duration", 100 + level * 40);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, dur, level, true, false, true));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, dur, level - 1, true, false, true));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, dur, level - 1, true, false, true));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, dur, level - 1, true, false, true));

        double aoeRadius = cfgd("aoe_radius", 6.0);
        double aoeDmg = cfgd("aoe_damage", 3.0 * level);
        for (LivingEntity e : MathUtil.getNearbyLiving(victim.getLocation(), aoeRadius)) {
            if (e.equals(victim)) continue;
            e.damage(aoeDmg, victim);
        }

        ParticleUtil.burst(victim.getLocation(), Particle.TOTEM_OF_UNDYING, 40, 3.0);
        ParticleUtil.drawCircle(victim.getLocation().add(0, 0.1, 0), aoeRadius, 30, Particle.FLAME);
        SoundUtil.play(victim.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        setCooldownFromConfig(victim, "cooldown", 90.0);
    }

    @Override public String getDescription(int level) {
        return "§7Below 20% HP: §6§lAPOTHEOSIS §7— all buffs + AoE burst. §890s CD.";
    }
}
