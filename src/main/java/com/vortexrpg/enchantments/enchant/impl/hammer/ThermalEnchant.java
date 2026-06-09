package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Thermal: Fire damage (ignite 2/3/4s) + melts nearby ice/snow in 3 blocks.
 */
public class ThermalEnchant extends VortexEnchant {

    public ThermalEnchant() {
        super("thermal", "Thermal", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int fireTicks = cfgi("fire_seconds", 1 + level) * 20;
        victim.setFireTicks(fireTicks);

        int radius = cfgi("melt_radius", 3);
        Location loc = victim.getLocation();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                    Material type = block.getType();
                    if (type == Material.ICE || type == Material.PACKED_ICE || type == Material.BLUE_ICE
                            || type == Material.SNOW_BLOCK || type == Material.SNOW) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }

        ParticleUtil.spawn(victim.getLocation(), Particle.FLAME, 12, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 0.8f, 1.0f);
    }

    @Override
    public String getDescription(int level) {
        int s = 1 + level;
        return "§7Ignites target §c" + s + "s §7+ melts ice/snow nearby.";
    }
}
