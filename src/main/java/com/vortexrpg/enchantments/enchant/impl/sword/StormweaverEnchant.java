package com.vortexrpg.enchantments.enchant.impl.sword;

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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stormweaver: On hit, summon lightning at the victim. Consecutive hits within
 * 5 seconds chain lightning to 1/2/3 nearby enemies.
 */
public class StormweaverEnchant extends VortexEnchant {

    private final ConcurrentHashMap<UUID, long[]> stormData = new ConcurrentHashMap<>();

    public StormweaverEnchant() {
        super("stormweaver", "Stormweaver", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 5.0);
        int chainTargets = cfgi("chain_targets", level);
        double chainRadius = cfgd("chain_radius", 5.0);
        double chainDamage = cfgd("chain_damage", 3.0);

        setCooldownSeconds(attacker, cooldown);

        victim.getWorld().strikeLightningEffect(victim.getLocation());
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.6f, 1.5f);

        UUID uuid = attacker.getUniqueId();
        long now = System.currentTimeMillis();
        long[] data = stormData.get(uuid);
        int chains;

        if (data != null && (now - data[0]) < 5000) {
            chains = Math.min((int) data[1] + 1, chainTargets);
        } else {
            chains = Math.min(1, chainTargets);
        }
        stormData.put(uuid, new long[]{now, chains});

        int chained = 0;
        for (Entity e : victim.getNearbyEntities(chainRadius, chainRadius, chainRadius)) {
            if (chained >= chains) break;
            if (e.equals(attacker) || !(e instanceof LivingEntity le)) continue;
            le.damage(chainDamage, attacker);
            ParticleUtil.drawLine(
                victim.getLocation().add(0, 1, 0),
                le.getLocation().add(0, 1, 0),
                Particle.ELECTRIC_SPARK, 0.5
            );
            chained++;
        }

        if (chained > 0) {
            attacker.sendMessage("§b[Stormweaver] §7Lightning chained to §e" + chained + "§7 enemies!");
        }
    }

    @Override
    public String getDescription(int level) {
        return "§7Summon §blightning§7 on hit. Chains to §e" + level + "§7 nearby enemies.";
    }
}
