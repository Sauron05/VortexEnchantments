package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Lockdown: Bolt creates a cage of barrier-like blocks around the target for 2s.
 * Uses cobblestone walls around the victim to trap them.
 */
public class LockdownEnchant extends VortexEnchant {

    public LockdownEnchant() {
        super("lockdown", "Lockdown", EnchantRarity.EPIC, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        int duration = cfgi("duration_ticks", 30 + level * 10);
        Location center = victim.getLocation();

        int[][] offsets = {{1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{1,0,1},{-1,0,-1},{1,0,-1},{-1,0,1}};
        List<Location> placed = new ArrayList<>();

        for (int[] o : offsets) {
            Location loc = center.clone().add(o[0], 0, o[2]);
            Location locUp = loc.clone().add(0, 1, 0);
            if (loc.getBlock().getType() == Material.AIR) {
                loc.getBlock().setType(Material.COBBLESTONE);
                placed.add(loc);
            }
            if (locUp.getBlock().getType() == Material.AIR) {
                locUp.getBlock().setType(Material.COBBLESTONE);
                placed.add(locUp);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location loc : placed) {
                    if (loc.getBlock().getType() == Material.COBBLESTONE) {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }.runTaskLater(plugin, duration);

        ParticleUtil.drawCircle(center, 1.5, 12, Particle.FLAME);
        SoundUtil.play(center, Sound.BLOCK_ANVIL_LAND, 0.5f, 1.5f);

        setCooldownFromConfig(shooter, "cooldown", 12.0);
    }

    @Override
    public String getDescription(int level) {
        double dur = (30 + level * 10) / 20.0;
        return "§7Bolt: §e§lLOCKDOWN §7— trap target in cage for §e" + dur + "s§7. 12s CD.";
    }
}
