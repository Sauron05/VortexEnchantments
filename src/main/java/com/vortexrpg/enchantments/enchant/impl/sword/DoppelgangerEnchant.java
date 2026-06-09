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
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

/**
 * Doppelganger: On kill, spawn a zombie clone of the victim that fights
 * for you for 10/15/20 seconds with their equipment.
 */
public class DoppelgangerEnchant extends VortexEnchant {

    public DoppelgangerEnchant() {
        super("doppelganger", "Doppelganger", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(killer)) return;

        double cooldown = cfgd("cooldown_seconds", 25.0);
        int durationTicks = cfgi("duration_ticks", 200) + (level - 1) * 100;

        setCooldownSeconds(killer, cooldown);

        Location spawnLoc = killed.getLocation();
        Zombie clone = spawnLoc.getWorld().spawn(spawnLoc, Zombie.class, z -> {
            z.customName(net.kyori.adventure.text.Component.text("§5" + killed.getName() + "'s Clone"));
            z.setCustomNameVisible(true);
            z.setAdult();
            z.setShouldBurnInDay(false);

            if (killed.getEquipment() != null) {
                var eq = z.getEquipment();
                eq.setHelmet(killed.getEquipment().getHelmet());
                eq.setChestplate(killed.getEquipment().getChestplate());
                eq.setLeggings(killed.getEquipment().getLeggings());
                eq.setBoots(killed.getEquipment().getBoots());
                eq.setItemInMainHand(killed.getEquipment().getItemInMainHand());
                eq.setHelmetDropChance(0f);
                eq.setChestplateDropChance(0f);
                eq.setLeggingsDropChance(0f);
                eq.setBootsDropChance(0f);
                eq.setItemInMainHandDropChance(0f);
            }
        });

        ParticleUtil.spawn(spawnLoc, Particle.SOUL, 20, 0.5);
        SoundUtil.play(spawnLoc, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.8f, 1.5f);
        killer.sendMessage("§5[Doppelganger] §7A clone of §e" + killed.getName() + " §7rises to serve you!");

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (clone.isValid() && !clone.isDead()) {
                ParticleUtil.spawn(clone.getLocation(), Particle.SOUL, 15, 0.5);
                clone.remove();
            }
        }, durationTicks);
    }

    @Override
    public String getDescription(int level) {
        int secs = 10 + (level - 1) * 5;
        return "§7On kill: spawn a §5zombie clone§7 of the victim for §e" + secs + "s§7.";
    }
}
