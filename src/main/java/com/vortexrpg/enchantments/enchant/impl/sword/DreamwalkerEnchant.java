package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;

/**
 * Dreamwalker: On hit, immerse target in a nightmare. Applies Darkness,
 * Nausea, and plays disturbing sounds around them for 3/4/5 seconds.
 */
public class DreamwalkerEnchant extends VortexEnchant {

    private static final Sound[] NIGHTMARE_SOUNDS = {
        Sound.ENTITY_GHAST_SCREAM, Sound.ENTITY_WARDEN_NEARBY_CLOSER,
        Sound.AMBIENT_CAVE, Sound.ENTITY_VEX_CHARGE, Sound.ENTITY_PHANTOM_BITE
    };
    private static final Random RANDOM = new Random();

    public DreamwalkerEnchant() {
        super("dreamwalker", "Dreamwalker", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 15.0);
        int durationTicks = cfgi("duration_ticks", 60) + (level - 1) * 20;

        setCooldownSeconds(attacker, cooldown);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, durationTicks, 0, false, false));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, durationTicks, 0, false, false));

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SCULK_SOUL, 15, 0.5);

        if (victim instanceof Player target) {
            BukkitTask[] task = new BukkitTask[1];
            final int[] ticks = {0};

            task[0] = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                if (ticks[0]++ >= durationTicks / 10 || !target.isOnline()) {
                    task[0].cancel();
                    return;
                }
                Sound s = NIGHTMARE_SOUNDS[RANDOM.nextInt(NIGHTMARE_SOUNDS.length)];
                double offX = (RANDOM.nextDouble() - 0.5) * 8;
                double offZ = (RANDOM.nextDouble() - 0.5) * 8;
                target.playSound(target.getLocation().add(offX, 0, offZ), s, 0.5f,
                    0.5f + RANDOM.nextFloat());
            }, 0L, 10L);
        }

        attacker.sendMessage("§5[Dreamwalker] §7Target trapped in a nightmare!");
    }

    @Override
    public String getDescription(int level) {
        int secs = 3 + (level - 1);
        return "§7Trap target in a §5nightmare§7 for §e" + secs + "s§7: Darkness + Nausea + phantom sounds.";
    }
}
