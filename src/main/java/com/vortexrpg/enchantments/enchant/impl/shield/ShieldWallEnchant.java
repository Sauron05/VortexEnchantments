package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Shield Wall: Blocking summons temp wall segments beside you. */
public class ShieldWallEnchant extends VortexEnchant {

    public ShieldWallEnchant() { super("shield_wall", "Shield Wall", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        if (isOnCooldown(player)) return;
        int height = cfgi("height", 1 + level);
        var dir = player.getLocation().getDirection().normalize();
        var right = dir.crossProduct(new org.bukkit.util.Vector(0, 1, 0)).normalize();
        Location center = player.getLocation().add(dir.multiply(2));
        var placed = new java.util.ArrayList<Block>();
        for (int w = -1; w <= 1; w++) {
            for (int h = 0; h < height; h++) {
                Location loc = center.clone().add(right.clone().multiply(w)).add(0, h, 0);
                Block b = loc.getBlock();
                if (b.getType().isAir()) {
                    b.setType(Material.COBBLESTONE_WALL);
                    placed.add(b);
                }
            }
        }
        int duration = cfgi("wall-duration", 40 + level * 20);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Block b : placed) {
                if (b.getType() == Material.COBBLESTONE_WALL) {
                    b.setType(Material.AIR);
                }
            }
        }, duration);
        ParticleUtil.burst(center, Particle.BLOCK, 20, 1.0);
        setCooldownFromConfig(player, "cooldown", 10);
    }

    @Override public String getDescription() { return "Blocking summons temporary wall."; }
    @Override public String getDescription(int level) {
        return "§7Block: summon §e3x" + (1 + level) + "§7 stone wall for §a" + ((40 + level * 20) / 20) + "s§7."; }
}
