package com.vortexrpg.enchantments.enchant.impl.sword;

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
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Architect: Right-click to place a temporary barrier (glass wall) in front of you.
 * Lasts 3/4/5 seconds, spans 3/4/5 blocks wide and 3 blocks tall.
 */
public class ArchitectEnchant extends VortexEnchant {

    public ArchitectEnchant() {
        super("architect", "Architect", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, ItemStack item, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double cooldown = cfgd("cooldown_seconds", 15.0);
        int width = cfgi("width", 2 + level);
        int height = cfgi("height", 3);
        int durationTicks = cfgi("duration_ticks", 60) + (level - 1) * 20;
        int distance = cfgi("distance", 2);

        setCooldownSeconds(player, cooldown);

        Vector dir = player.getLocation().getDirection().normalize();
        dir.setY(0).normalize();
        Vector right = new Vector(-dir.getZ(), 0, dir.getX());

        Location center = player.getLocation().add(dir.clone().multiply(distance));
        List<Location> barrierBlocks = new ArrayList<>();

        int halfWidth = width / 2;
        for (int w = -halfWidth; w <= halfWidth; w++) {
            for (int h = 0; h < height; h++) {
                Location loc = center.clone().add(right.clone().multiply(w)).add(0, h, 0);
                Block block = loc.getBlock();
                if (block.getType() == Material.AIR) {
                    block.setType(Material.GLASS);
                    barrierBlocks.add(loc);
                }
            }
        }

        SoundUtil.play(center, Sound.BLOCK_GLASS_PLACE, 1.0f, 0.8f);
        ParticleUtil.spawn(center.add(0, 1, 0), Particle.END_ROD, 10, 1.0);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Location loc : barrierBlocks) {
                if (loc.getBlock().getType() == Material.GLASS) {
                    loc.getBlock().setType(Material.AIR);
                    ParticleUtil.spawn(loc, Particle.BLOCK, 5, 0.3,
                        Material.GLASS.createBlockData());
                }
            }
            SoundUtil.play(center, Sound.BLOCK_GLASS_BREAK, 0.8f, 1.2f);
        }, durationTicks);

        player.sendMessage("§5[Architect] §7Barrier erected! Lasts §e" + (durationTicks / 20) + "s§7.");
    }

    @Override
    public String getDescription(int level) {
        int secs = 3 + (level - 1);
        int width = 2 + level;
        return "§7Right-click: place a §fglass barrier§7 (" + width + " wide) for §e" + secs + "s§7.";
    }
}
