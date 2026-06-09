package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;

/**
 * Siphon: 15/25/35% chance on hit to steal one random positive potion
 * effect from the target and apply it to yourself.
 */
public class SiphonEnchant extends VortexEnchant {

    public SiphonEnchant() {
        super("siphon", "Siphon", EnchantRarity.RARE, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double chance = cfgd("chance", 0.05 + level * 0.10);
        if (Math.random() > chance) return;

        Collection<PotionEffect> effects = victim.getActivePotionEffects();
        List<PotionEffect> positives = effects.stream()
                .filter(e -> isPositiveEffect(e.getType()))
                .toList();

        if (positives.isEmpty()) return;

        PotionEffect stolen = positives.get((int) (Math.random() * positives.size()));
        victim.removePotionEffect(stolen.getType());
        attacker.addPotionEffect(new PotionEffect(stolen.getType(), stolen.getDuration(),
                stolen.getAmplifier(), false, true));

        ParticleUtil.drawLine(victim.getLocation().add(0, 1, 0),
                attacker.getLocation().add(0, 1, 0), Particle.WITCH, 0.3);
        SoundUtil.play(attacker.getLocation(), Sound.ENTITY_WITCH_DRINK, 0.7f, 1.5f);
    }

    private boolean isPositiveEffect(PotionEffectType type) {
        return type.equals(PotionEffectType.SPEED) || type.equals(PotionEffectType.STRENGTH)
                || type.equals(PotionEffectType.REGENERATION) || type.equals(PotionEffectType.RESISTANCE)
                || type.equals(PotionEffectType.FIRE_RESISTANCE) || type.equals(PotionEffectType.ABSORPTION)
                || type.equals(PotionEffectType.JUMP_BOOST) || type.equals(PotionEffectType.HASTE);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.10) * 100);
        return "§7" + pct + "% chance to §5steal §7a potion effect from target.";
    }
}
