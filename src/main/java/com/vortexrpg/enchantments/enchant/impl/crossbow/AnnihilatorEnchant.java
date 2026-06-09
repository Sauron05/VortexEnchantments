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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Annihilator: Holding crossbow charged for 2+ seconds before firing deals
 * 50/75/100% bonus damage. Rewards patience.
 */
public class AnnihilatorEnchant extends VortexEnchant {

    private static final Map<UUID, Long> CHARGE_START = new HashMap<>();

    public AnnihilatorEnchant() {
        super("annihilator", "Annihilator", EnchantRarity.EPIC, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onShoot(org.bukkit.event.entity.EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        CHARGE_START.put(shooter.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        Long chargeTime = CHARGE_START.remove(shooter.getUniqueId());
        if (chargeTime == null) return;

        long held = System.currentTimeMillis() - chargeTime;
        long threshold = cfgi("charge_ms", 2000);
        if (held < threshold) return;

        double bonus = cfgd("bonus", 0.25 + level * 0.25);
        event.setDamage(event.getDamage() * (1.0 + bonus));

        ParticleUtil.burst(victim.getLocation().add(0, 1, 0), Particle.EXPLOSION, 2, 0.3);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.6f, 1.8f);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.25 + level * 0.25) * 100);
        return "§7Charged shot (2s+): §c+" + pct + "% §7damage.";
    }
}
