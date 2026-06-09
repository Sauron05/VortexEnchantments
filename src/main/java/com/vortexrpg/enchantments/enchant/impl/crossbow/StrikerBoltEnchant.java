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
 * StrikerBolt: First bolt hit after reloading deals 15/20/25% bonus damage.
 * Rewards deliberate, one-shot-at-a-time play.
 */
public class StrikerBoltEnchant extends VortexEnchant {

    private static final Map<UUID, Boolean> FRESH_LOAD = new HashMap<>();

    public StrikerBoltEnchant() {
        super("strikerbolt", "Striker Bolt", EnchantRarity.COMMON, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onShoot(org.bukkit.event.entity.EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        FRESH_LOAD.put(shooter.getUniqueId(), true);
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        Boolean fresh = FRESH_LOAD.remove(shooter.getUniqueId());
        if (fresh == null || !fresh) return;

        double bonus = cfgd("bonus", 0.10 + level * 0.05);
        event.setDamage(event.getDamage() * (1.0 + bonus));

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 8, 0.3);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.8f, 1.5f);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.05) * 100);
        return "§7First bolt after reload: §e+" + pct + "% §7damage.";
    }
}
