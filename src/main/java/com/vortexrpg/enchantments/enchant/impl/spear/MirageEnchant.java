package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Mirage: On hit, spawn 2/3/4 decoy armor stands around the attacker to
 * confuse enemy AI and players. Decoys vanish after 3 seconds.
 */
public class MirageEnchant extends VortexEnchant {

    public MirageEnchant() {
        super("mirage", "Mirage", EnchantRarity.EPIC, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        int decoys = cfgi("decoys", 1 + level);
        int lifetimeTicks = cfgi("lifetime_ticks", 60);

        SoundUtil.play(attacker.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.8f, 1.2f);

        for (int i = 0; i < decoys; i++) {
            double angle = (2 * Math.PI / decoys) * i;
            double x = Math.cos(angle) * 2.0;
            double z = Math.sin(angle) * 2.0;

            Location spawnLoc = attacker.getLocation().add(x, 0, z);
            ArmorStand decoy = spawnLoc.getWorld().spawn(spawnLoc, ArmorStand.class, as -> {
                as.setVisible(true);
                as.setGravity(false);
                as.setInvulnerable(true);
                as.setBasePlate(false);
                as.customName(net.kyori.adventure.text.Component.text("§7" + attacker.getName()));
                as.setCustomNameVisible(true);
            });

            ParticleUtil.spawn(spawnLoc, Particle.CLOUD, 8, 0.3);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (decoy.isValid()) {
                        ParticleUtil.spawn(decoy.getLocation(), Particle.POOF, 10, 0.3);
                        decoy.remove();
                    }
                }
            }.runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), lifetimeTicks);
        }

        setCooldownFromConfig(attacker, "cooldown", 10);
    }

    @Override
    public String getDescription(int level) {
        return "§7On hit: spawn §d" + (1 + level) + " decoy copies §7to confuse enemies. §8(10s CD)";
    }
}
