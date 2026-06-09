package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
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
 * Hypnosis: When hit, X% chance to confuse the attacker (Slowness + Nausea). 8s CD.
 */
public class HypnosisEnchant extends VortexEnchant {
    public HypnosisEnchant() { super("hypnosis", "Hypnosis", EnchantRarity.EPIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!(attacker instanceof LivingEntity living)) return;
        if (isOnCooldown(victim)) return;
        double chance = cfgd("chance", 0.15 + level * 0.10);
        if (Math.random() >= chance) return;

        int dur = cfgi("duration", 40 + level * 20);
        living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, dur, 1));
        living.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, dur, 0));
        ParticleUtil.spawn(living.getLocation().add(0, 1, 0), Particle.WITCH, 10, 0.4);
        SoundUtil.play(living.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.6f, 0.8f);
        setCooldownFromConfig(victim, "cooldown", 8.0);
    }

    @Override public String getDescription(int level) {
        int pct = (int) ((0.15 + level * 0.10) * 100);
        return "§a" + pct + "%§7 chance to inflict §cSlowness + Nausea §7on attacker. §88s CD.";
    }
}
