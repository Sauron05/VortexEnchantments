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

import java.util.List;

/**
 * World Sense: Passively sense ALL damage in a massive radius.
 * When any mob takes damage near you, you gain a small heal.
 * On hit, trigger a shockwave that damages all enemies proportional to
 * the total damage absorbed. 45s CD.
 */
public class WorldSenseEnchant extends VortexEnchant {
    private static final java.util.Map<java.util.UUID, Double> ABSORBED = new java.util.HashMap<>();

    public WorldSenseEnchant() { super("world_sense", "World Sense", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double radius = cfgd("sense_radius", 20.0 + level * 10.0);
        double healPer = cfgd("heal_per_entity", 0.1);
        int count = 0;
        for (LivingEntity e : player.getWorld().getNearbyLivingEntities(player.getLocation(), radius)) {
            if (e instanceof Player || e.equals(player)) continue;
            if (e.getNoDamageTicks() > 0) count++;
        }
        if (count > 0) {
            ABSORBED.merge(player.getUniqueId(), count * healPer, (a, b) -> a + b);
            double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
            if (player.getHealth() < maxHp) {
                player.setHealth(Math.min(maxHp, player.getHealth() + count * healPer));
            }
        }
        ParticleUtil.drawCircle(player.getLocation().add(0, 0.1, 0), radius, 20, Particle.ENCHANT);
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(victim)) return;
        Double stored = ABSORBED.remove(victim.getUniqueId());
        if (stored == null || stored < 2.0) return;

        double radius = cfgd("burst_radius", 6.0);
        double dmg = Math.min(stored * 2.0, cfgd("max_burst", 20.0));

        for (LivingEntity e : com.vortexrpg.enchantments.util.MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (e.equals(victim)) continue;
            e.damage(dmg, victim);
        }
        ParticleUtil.burst(victim.getLocation(), Particle.SCULK_SOUL, 40, 3.0);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 0.8f);
        setCooldownFromConfig(victim, "cooldown", 45.0);
    }

    @Override public String getDescription(int level) {
        return "§7Sense damage in §a" + (int)(20 + level * 10) + "§7 block radius. On hit: §5§lBURST §7stored energy. §845s CD.";
    }
}
