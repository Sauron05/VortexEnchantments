package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
 * Eclipse: Right-click to create a Darkness dome — 6/8/10 block radius for 6 seconds.
 * While inside YOUR dome: +30/45/60% damage + 20% lifesteal.
 * 25-second cooldown.
 */
public class EclipseEnchant extends VortexEnchant {

    private static final Set<UUID> IN_DOME = ConcurrentHashMap.newKeySet();

    public EclipseEnchant() {
        super("eclipse", "Eclipse", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double radius = cfgd("radius", 4.0 + level * 2.0);
        int durationTicks = cfgi("duration_ticks", 120);
        var center = player.getLocation().clone();

        IN_DOME.add(player.getUniqueId());

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks += 10;
                if (ticks > durationTicks || !player.isValid()) {
                    IN_DOME.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                ParticleUtil.drawCircle(center, radius, 24, Particle.SMOKE);

                for (LivingEntity e : MathUtil.getNearbyLiving(center, radius)) {
                    if (e.equals(player)) continue;
                    e.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 30, 0, false, false));
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);

        SoundUtil.play(center, Sound.ENTITY_WARDEN_EMERGE, 0.7f, 0.5f);
        setCooldownFromConfig(player, "cooldown", 25);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!IN_DOME.contains(attacker.getUniqueId())) return;

        double bonus = cfgd("dome_bonus", 0.15 + level * 0.15);
        double lifesteal = cfgd("lifesteal", 0.20);

        event.setDamage(event.getDamage() * (1.0 + bonus));

        double heal = event.getDamage() * lifesteal;
        double maxHp = attacker.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        attacker.setHealth(Math.min(attacker.getHealth() + heal, maxHp));

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SMOKE, 6, 0.3);
    }

    @Override
    public String getDescription(int level) {
        int r = 4 + level * 2;
        int pct = (int) ((0.15 + level * 0.15) * 100);
        return "§7Right-click: §8Darkness dome §7" + r + " blocks. Inside: §c+" + pct + "% §7+ §a20% lifesteal§7. §8(25s CD)";
    }
}
