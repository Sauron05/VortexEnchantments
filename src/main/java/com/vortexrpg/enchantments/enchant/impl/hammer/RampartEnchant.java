package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Rampart: Right-click to summon a cobblestone wall (3/5/7 blocks tall) for 4 seconds.
 * 15-second cooldown. The wall auto-removes.
 */
public class RampartEnchant extends VortexEnchant {

    public RampartEnchant() {
        super("rampart", "Rampart", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        int height = cfgi("height", 1 + level * 2);
        int duration = cfgi("duration_ticks", 80);

        Location base = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(2));
        base.setY(player.getLocation().getBlockY());

        // Build wall perpendicular to player facing
        double yaw = Math.toRadians(player.getLocation().getYaw() + 90);
        double dx = Math.cos(yaw);
        double dz = Math.sin(yaw);

        List<Block> placed = new ArrayList<>();
        for (int w = -1; w <= 1; w++) {
            for (int h = 0; h < height; h++) {
                Block block = base.getWorld().getBlockAt(
                        (int) (base.getBlockX() + w * dx),
                        base.getBlockY() + h,
                        (int) (base.getBlockZ() + w * dz)
                );
                if (block.getType().isAir()) {
                    block.setType(Material.COBBLESTONE);
                    placed.add(block);
                }
            }
        }

        SoundUtil.play(base, Sound.BLOCK_STONE_PLACE, 1.0f, 0.8f);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block b : placed) {
                    if (b.getType() == Material.COBBLESTONE) {
                        b.setType(Material.AIR);
                    }
                }
            }
        }.runTaskLater(plugin, duration);

        setCooldownFromConfig(player, "cooldown", 15);
    }

    @Override
    public String getDescription(int level) {
        int h = 1 + level * 2;
        return "§7Right-click: summon §e" + h + "-high §7cobble wall for §e4s§7. §8(15s CD)";
    }
}
