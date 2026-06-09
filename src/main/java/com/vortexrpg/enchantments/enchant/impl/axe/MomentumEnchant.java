package com.vortexrpg.enchantments.enchant.impl.axe;

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

import java.util.List;

/**
 * Momentum: Each hit grants stacking Speed buff.
 * Level 1: Speed I for 3s per hit. Level 2: Speed II for 3s. Level 3: Speed II for 4s.
 */
public class MomentumEnchant extends VortexEnchant {

    public MomentumEnchant() {
        super("momentum", "Momentum", EnchantRarity.RARE, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int speedLevel = cfgi("speed_level", level >= 2 ? 1 : 0);
        int durationTicks = cfgi("duration_ticks", level >= 3 ? 80 : 60);

        PotionEffect existing = attacker.getPotionEffect(PotionEffectType.SPEED);
        int newDuration = durationTicks;
        if (existing != null) {
            newDuration = Math.min(existing.getDuration() + durationTicks, durationTicks * 4);
        }

        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, newDuration, speedLevel, false, false));

        ParticleUtil.spawn(attacker.getLocation().add(0, 0.5, 0), Particle.CLOUD, 3, 0.2);
        SoundUtil.play(attacker.getLocation(), Sound.ENTITY_HORSE_GALLOP, 0.3f, 1.5f);
    }

    @Override
    public String getDescription(int level) {
        String tier = level >= 2 ? "II" : "I";
        return "§7Hits grant §bSpeed " + tier + "§7, stacking duration up to §e4x§7.";
    }
}
