package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Tremorsense: Passive — every 3s, highlight (Glowing) all entities within 8/12/16 blocks.
 * Implemented via onAttack trigger that starts a repeating task on first hit.
 */
public class TremorsenseEnchant extends VortexEnchant {

    private static final java.util.Set<java.util.UUID> ACTIVE = java.util.concurrent.ConcurrentHashMap.newKeySet();

    public TremorsenseEnchant() {
        super("tremorsense", "Tremorsense", EnchantRarity.RARE, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (ACTIVE.contains(attacker.getUniqueId())) return;

        ACTIVE.add(attacker.getUniqueId());
        double radius = cfgd("radius", 4.0 + level * 4.0);
        int interval = cfgi("interval_ticks", 60);
        int duration = cfgi("duration_ticks", 200);

        new BukkitRunnable() {
            int elapsed = 0;

            @Override
            public void run() {
                elapsed += interval;
                if (elapsed > duration || !attacker.isOnline()) {
                    ACTIVE.remove(attacker.getUniqueId());
                    cancel();
                    return;
                }
                for (var entity : attacker.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity living && !living.equals(attacker)) {
                        living.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, interval + 10, 0, false, false));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, interval);
    }

    @Override
    public String getDescription(int level) {
        int r = 4 + level * 4;
        return "§7After hitting: highlight all entities within §e" + r + " blocks §7for 10s.";
    }
}
