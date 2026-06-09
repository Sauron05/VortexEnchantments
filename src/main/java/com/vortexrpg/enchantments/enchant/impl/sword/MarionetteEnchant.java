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
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Marionette: On hit, confuse the target so nearby mobs turn hostile toward them
 * for 2/3/4 seconds. Mobs within range retarget to the victim.
 */
public class MarionetteEnchant extends VortexEnchant {

    public MarionetteEnchant() {
        super("marionette", "Marionette", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 12.0);
        double range = cfgd("range", 8.0);
        int durationTicks = cfgi("duration_ticks", 40) + (level - 1) * 20;

        setCooldownSeconds(attacker, cooldown);

        ParticleUtil.spawn(victim.getLocation().add(0, 2, 0), Particle.WITCH, 15, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 0.8f, 1.0f);

        int redirected = 0;
        for (Entity e : victim.getNearbyEntities(range, range, range)) {
            if (!(e instanceof Mob mob)) continue;
            if (e.equals(attacker)) continue;
            mob.setTarget(victim);
            redirected++;
        }

        if (redirected > 0) {
            attacker.sendMessage("§5[Marionette] §7Turned §e" + redirected + "§7 mobs against the target!");
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Entity e : victim.getNearbyEntities(range, range, range)) {
                if (e instanceof Mob mob && mob.getTarget() != null && mob.getTarget().equals(victim)) {
                    mob.setTarget(null);
                }
            }
        }, durationTicks);
    }

    @Override
    public String getDescription(int level) {
        int secs = 2 + (level - 1);
        return "§7Nearby mobs turn §5hostile§7 toward the target for §e" + secs + "s§7.";
    }
}
