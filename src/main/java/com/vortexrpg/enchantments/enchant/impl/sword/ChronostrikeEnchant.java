package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Chronostrike: Freezes the target in place for 1/1.5/2 seconds.
 * Target is teleported back if they move during the freeze.
 */
public class ChronostrikeEnchant extends VortexEnchant {

    public ChronostrikeEnchant() {
        super("chronostrike", "Chronostrike", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 10.0);
        int freezeTicks = cfgi("freeze_ticks", 20) + (level - 1) * 10;

        setCooldownSeconds(attacker, cooldown);

        Location frozenLoc = victim.getLocation().clone();
        victim.setFreezeTicks(freezeTicks + 40);

        ParticleUtil.spawn(frozenLoc, Particle.SNOWFLAKE, 20, 0.5);
        SoundUtil.play(frozenLoc, Sound.BLOCK_GLASS_BREAK, 0.8f, 0.5f);

        BukkitTask[] task = new BukkitTask[1];
        final int[] ticks = {0};

        task[0] = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (ticks[0]++ >= freezeTicks / 2 || !victim.isValid() || victim.isDead()) {
                task[0].cancel();
                return;
            }
            if (victim.getLocation().distanceSquared(frozenLoc) > 0.25) {
                victim.teleport(frozenLoc);
            }
            ParticleUtil.spawn(frozenLoc.clone().add(0, 1, 0), Particle.SNOWFLAKE, 3, 0.3);
        }, 0L, 2L);

        if (victim instanceof Player p) {
            p.sendMessage("§b[Chronostrike] §7Time frozen around you!");
        }
    }

    @Override
    public String getDescription(int level) {
        double secs = 1.0 + (level - 1) * 0.5;
        return "§7Freezes target in §btime§7 for §e" + secs + "s§7. They cannot move.";
    }
}
