package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Symbiote: Link with hit target for 5 seconds. You heal 10/15/20% of
 * all damage the target takes from any source during the link.
 */
public class SymbioteEnchant extends VortexEnchant {

    private final ConcurrentHashMap<UUID, UUID> links = new ConcurrentHashMap<>();

    public SymbioteEnchant() {
        super("symbiote", "Symbiote", EnchantRarity.RARE, 3, List.of(ItemTarget.SWORD));
    }

    public UUID getLinkedPlayer(UUID victimUuid) {
        return links.get(victimUuid);
    }

    public double getHealRatio(int level) {
        return cfgd("heal_ratio", 0.05 + level * 0.05);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 10.0);
        int durationTicks = cfgi("duration_ticks", 100);

        setCooldownSeconds(attacker, cooldown);

        links.put(victim.getUniqueId(), attacker.getUniqueId());
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.HEART, 5, 0.5);
        attacker.sendMessage("§5[Symbiote] §7Linked! You heal from their pain.");

        BukkitTask[] task = new BukkitTask[1];
        final int[] ticks = {0};

        task[0] = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (ticks[0]++ >= durationTicks / 4 || !victim.isValid() || victim.isDead()) {
                links.remove(victim.getUniqueId());
                task[0].cancel();
                return;
            }
            ParticleUtil.drawLine(
                attacker.getLocation().add(0, 1, 0),
                victim.getLocation().add(0, 1, 0),
                Particle.HEART, 2.0
            );
        }, 0L, 4L);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.05) * 100);
        return "§7Link with target: heal §a" + pct + "%§7 of all damage they take for §e5s§7.";
    }
}
