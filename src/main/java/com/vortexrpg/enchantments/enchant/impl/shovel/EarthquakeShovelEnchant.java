package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Earthquake Shovel: Massive 5×5 break + damage + knockup. */
public class EarthquakeShovelEnchant extends VortexEnchant {

    public EarthquakeShovelEnchant() { super("earthquake_shovel", "Earthquake Shovel", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(player)) return;
        int radius = cfgi("radius", 2 + level);
        double damage = cfg("damage", 4.0 + level * 2);
        Block center = event.getBlock();
        Location loc = center.getLocation().add(0.5, 0.5, 0.5);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block b = center.getRelative(x, 0, z);
                if (isShovelBlock(b.getType()) && !b.equals(center)) {
                    b.breakNaturally(player.getInventory().getItemInMainHand());
                }
            }
        }
        SoundUtil.play(loc, Sound.ENTITY_RAVAGER_ROAR, 1.0f, 0.6f);
        ParticleUtil.drawCircle(loc, radius + 1, 40, Particle.CAMPFIRE_COSY_SMOKE);
        for (LivingEntity e : MathUtil.getNearbyLiving(loc, radius + 1)) {
            if (e.equals(player)) continue;
            e.damage(damage, player);
            e.setVelocity(e.getVelocity().add(new Vector(0, 0.8, 0)));
        }
        setCooldownFromConfig(player, "cooldown", 12);
    }

    private boolean isShovelBlock(Material mat) {
        return mat == Material.DIRT || mat == Material.GRASS_BLOCK || mat == Material.SAND
                || mat == Material.RED_SAND || mat == Material.GRAVEL || mat == Material.CLAY
                || mat == Material.SOUL_SAND || mat == Material.SOUL_SOIL || mat == Material.MUD
                || mat == Material.SNOW_BLOCK || mat == Material.COARSE_DIRT || mat == Material.PODZOL
                || mat == Material.MYCELIUM || mat == Material.ROOTED_DIRT || mat == Material.FARMLAND;
    }

    @Override public String getDescription() { return "Massive area break with earthquake damage."; }
    @Override public String getDescription(int level) {
        return "§7Quake: break " + (2 + level) + "×" + (2 + level) + " area + §c" + (int)(4 + level * 2) + "♥§7 + knockup."; }
}
