package com.vortexrpg.enchantments.enchant.impl.crossbow;

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
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

/**
 * Nemesis: Revenge-based bolt — the more damage the shooter has taken from
 * a specific target, the more damage the bolt deals in return.
 * Builds grudge over the fight.
 */
public class NemesisEnchant extends VortexEnchant {

    private static final String META_NEMESIS = "vortex_nemesis_";

    public NemesisEnchant() {
        super("nemesis", "Nemesis", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, org.bukkit.entity.Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!(attacker instanceof LivingEntity)) return;

        String key = META_NEMESIS + attacker.getUniqueId();
        double accumulated = 0;
        if (victim.hasMetadata(key)) {
            accumulated = victim.getMetadata(key).getFirst().asDouble();
        }
        accumulated += event.getFinalDamage();
        victim.setMetadata(key, new FixedMetadataValue(plugin, accumulated));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        String key = META_NEMESIS + victim.getUniqueId();
        double grudge = 0;
        if (shooter.hasMetadata(key)) {
            grudge = shooter.getMetadata(key).getFirst().asDouble();
        }

        if (grudge <= 0) return;

        double conversionRate = cfgd("conversion_rate", 0.15 + level * 0.10);
        double maxBonus = cfgd("max_bonus", 15.0);
        double bonus = Math.min(grudge * conversionRate, maxBonus);

        event.setDamage(event.getDamage() + bonus);

        // Consume partial grudge
        double remaining = grudge * cfgd("grudge_retain", 0.5);
        shooter.setMetadata(key, new FixedMetadataValue(plugin, remaining));

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ANGRY_VILLAGER, 5, 0.3);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SOUL_FIRE_FLAME, 10, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_WARDEN_ANGRY, 0.5f, 1.5f);
    }

    @Override
    public String getDescription(int level) {
        int pct = 15 + level * 10;
        return "§7Bolt: §4§lNEMESIS §7— converts §c" + pct + "% §7of damage taken from target into bonus damage.";
    }
}
