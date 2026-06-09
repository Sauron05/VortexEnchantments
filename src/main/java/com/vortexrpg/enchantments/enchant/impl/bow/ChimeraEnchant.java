package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Chimera: Each successive arrow shot alternates between fire / ice / lightning.
 * Fire = ignite, Ice = Slowness + Freeze, Lightning = bonus damage + strike visual.
 */
public class ChimeraEnchant extends VortexEnchant {

    private static final Map<UUID, Integer> CYCLE = new HashMap<>();

    public ChimeraEnchant() {
        super("chimera", "Chimera", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int phase = CYCLE.merge(shooter.getUniqueId(), 1, (a, b) -> a + b) % 3;

        switch (phase) {
            case 0 -> applyFire(victim, level);
            case 1 -> applyIce(victim, level);
            case 2 -> applyLightning(event, victim, level);
        }
    }

    private void applyFire(LivingEntity victim, int level) {
        int ticks = cfgi("fire_ticks", 40 + level * 20);
        victim.setFireTicks(ticks);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.FLAME, 12, 0.4);
    }

    private void applyIce(LivingEntity victim, int level) {
        int freezeTicks = cfgi("freeze_ticks", 40 + level * 10);
        victim.setFreezeTicks(freezeTicks);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SNOWFLAKE, 12, 0.4);
    }

    private void applyLightning(EntityDamageByEntityEvent event, LivingEntity victim, int level) {
        double bonusDmg = cfgd("lightning_bonus", 1.0 + level);
        event.setDamage(event.getDamage() + bonusDmg);
        victim.getWorld().strikeLightningEffect(victim.getLocation());
    }

    @Override
    public String getDescription(int level) {
        return "§7Arrows cycle: §6fire §7→ §bice §7→ §e⚡lightning§7.";
    }
}
