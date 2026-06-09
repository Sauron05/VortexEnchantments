package com.vortexrpg.enchantments.enchant.impl.axe;

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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Thorncaller: On hit, summon rising thorn particles beneath the target
 * that deal damage after a short delay.
 */
public class ThorncallerEnchant extends VortexEnchant {

    public ThorncallerEnchant() {
        super("thorncaller", "Thorncaller", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double thornDmg = cfgd("thorn_damage", 2.0 + level * 1.5);
        int delayTicks = cfgi("delay_ticks", 15);
        Location thornLoc = victim.getLocation();

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= delayTicks) {
                    ParticleUtil.spawn(thornLoc.clone().add(0, 1, 0), Particle.HAPPY_VILLAGER, 20, 0.5);
                    SoundUtil.play(thornLoc, Sound.BLOCK_SWEET_BERRY_BUSH_BREAK, 1.2f, 0.5f);

                    if (victim.isValid() && !victim.isDead() && victim.getLocation().distanceSquared(thornLoc) < 9) {
                        victim.damage(thornDmg, attacker);
                    }
                    cancel();
                    return;
                }

                ParticleUtil.spawn(thornLoc.clone().add(0, ticks * 0.15, 0),
                        Particle.HAPPY_VILLAGER, 3, 0.2);
                ticks += 3;
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 0, 3);

        setCooldownFromConfig(attacker, "cooldown", 4);
    }

    @Override
    public String getDescription(int level) {
        double dmg = 2.0 + level * 1.5;
        return "§7Summon thorns that erupt after a delay for §c" + dmg + " damage§7.";
    }
}
