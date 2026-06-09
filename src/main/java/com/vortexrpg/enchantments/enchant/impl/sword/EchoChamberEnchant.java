package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Echo Chamber: Each hit within a 3-second window produces 1/2/3 phantom echo hits
 * at 30% of the original damage. Creates a chain-hit effect.
 */
public class EchoChamberEnchant extends VortexEnchant {

    private final ConcurrentHashMap<UUID, Long> lastHitTime = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Boolean> isEchoing = new ConcurrentHashMap<>();

    public EchoChamberEnchant() {
        super("echo_chamber", "Echo Chamber", EnchantRarity.RARE, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        UUID uuid = attacker.getUniqueId();
        if (Boolean.TRUE.equals(isEchoing.get(uuid))) return;

        int echoCount = cfgi("echo_count", level);
        double echoRatio = cfgd("echo_ratio", 0.3);
        long windowMillis = (long) (cfgd("window_seconds", 3.0) * 1000);
        int echoDelay = cfgi("echo_delay_ticks", 5);

        long now = System.currentTimeMillis();
        Long lastHit = lastHitTime.get(uuid);

        if (lastHit == null || (now - lastHit) > windowMillis) {
            lastHitTime.put(uuid, now);
            return;
        }

        lastHitTime.put(uuid, now);
        double echoDamage = event.getDamage() * echoRatio;

        for (int i = 1; i <= echoCount; i++) {
            int delay = i * echoDelay;
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (!victim.isValid() || victim.isDead()) return;
                isEchoing.put(uuid, true);
                victim.damage(echoDamage, attacker);
                isEchoing.remove(uuid);
                ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SONIC_BOOM, 1, 0.1);
            }, delay);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) (0.3 * 100);
        return "§7Hits within 3s echo §e" + level + "x§7 at §c" + pct + "%§7 damage each.";
    }
}
