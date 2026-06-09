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

import java.util.List;

/**
 * Retrograde: When your thrown trident hits an entity, teleport yourself
 * to the target's location. Aggressive gap-closer.
 */
public class RetrogradeEnchant extends VortexEnchant {

    public RetrogradeEnchant() {
        super("retrograde", "Retrograde", EnchantRarity.RARE, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        ParticleUtil.spawn(shooter.getLocation(), Particle.REVERSE_PORTAL, 20, 0.5);
        SoundUtil.play(shooter.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1.2f);

        shooter.teleport(victim.getLocation());

        ParticleUtil.spawn(shooter.getLocation(), Particle.REVERSE_PORTAL, 20, 0.5);
        SoundUtil.play(shooter.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1.2f);

        double cooldown = cfgd("cooldown", 16.0 - level);
        setCooldownFromConfig(shooter, "cooldown", cooldown);
    }

    @Override
    public String getDescription(int level) {
        int cd = 16 - level;
        return "§7Thrown hit §dteleports you §7to the target. §8(" + cd + "s CD)";
    }
}
