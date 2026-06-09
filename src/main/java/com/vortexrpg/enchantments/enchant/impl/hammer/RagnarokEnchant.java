package com.vortexrpg.enchantments.enchant.impl.hammer;

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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ragnarok: Right-click to enter boss mode for 8 seconds — Strength II, Resistance I,
 * Speed I, Fire Resistance, +50/75/100% damage, and immune to knockback.
 * 60-second cooldown. The ultimate warrior buff.
 */
public class RagnarokEnchant extends VortexEnchant {

    private static final Set<UUID> ACTIVE = ConcurrentHashMap.newKeySet();

    public RagnarokEnchant() {
        super("ragnarok", "Ragnarok", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;
        if (ACTIVE.contains(player.getUniqueId())) return;

        int durationTicks = cfgi("duration_ticks", 160);
        ACTIVE.add(player.getUniqueId());

        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, durationTicks, 1, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationTicks, 0, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, durationTicks, 0, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, durationTicks, 0, false, true));

        ParticleUtil.spawn(player.getLocation(), Particle.FLAME, 30, 1.0);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.6f, 1.5f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks += 4;
                if (ticks > durationTicks || !player.isValid()) {
                    ACTIVE.remove(player.getUniqueId());
                    cancel();
                    return;
                }
                ParticleUtil.spawn(player.getLocation().add(0, 1, 0), Particle.FLAME, 4, 0.5);
            }
        }.runTaskTimer(plugin, 0L, 4L);

        setCooldownFromConfig(player, "cooldown", 60);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!ACTIVE.contains(attacker.getUniqueId())) return;

        double bonus = cfgd("rage_bonus", 0.25 + level * 0.25);
        event.setDamage(event.getDamage() * (1.0 + bonus));
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!ACTIVE.contains(victim.getUniqueId())) return;

        // Negate knockback
        new BukkitRunnable() {
            @Override
            public void run() {
                if (victim.isOnline()) {
                    victim.setVelocity(victim.getVelocity().setX(0).setZ(0));
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.25 + level * 0.25) * 100);
        return "§7Right-click: §4Boss mode 8s §7— all buffs + §c+" + pct + "% §7dmg. §8(60s CD)";
    }
}
