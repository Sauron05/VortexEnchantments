package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Doom: Marks the target. After 3 seconds, detonates for massive damage.
 * If the target dies before detonation, the mark is wasted.
 * Doom damage: 6/9/12.
 */
public class DoomEnchant extends VortexEnchant {

    public DoomEnchant() {
        super("doom", "Doom", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double chance = cfgd("chance", 0.2);
        if (Math.random() > chance) return;

        double doomDamage = cfgd("doom_damage", 3.0 + level * 3.0);
        int delayTicks = cfgi("delay_ticks", 60);

        if (victim instanceof Player p) {
            p.sendMessage("§4[DOOM] §cYou have been marked for death!");
        }
        attacker.sendMessage("§4[Doom] §7Target marked. Detonation in 3s...");

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!victim.isValid() || victim.isDead()) {
                    cancel();
                    return;
                }

                if (tick < delayTicks) {
                    if (tick % 10 == 0) {
                        ParticleUtil.spawn(victim.getLocation().add(0, 2.2, 0), Particle.ANGRY_VILLAGER, 3, 0.2);
                    }
                    tick += 5;
                    return;
                }

                victim.damage(doomDamage, attacker);
                ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.EXPLOSION, 3, 0.5);
                ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.LARGE_SMOKE, 20, 0.8);
                SoundUtil.play(victim.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 0.8f, 0.6f);
                cancel();
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 0, 5);

        setCooldownFromConfig(attacker, "cooldown", 10);
    }

    @Override
    public String getDescription(int level) {
        double dmg = 3.0 + level * 3.0;
        return "§7Marks target for §4DOOM§7. After 3s, deals §c" + dmg + " damage§7.";
    }
}
