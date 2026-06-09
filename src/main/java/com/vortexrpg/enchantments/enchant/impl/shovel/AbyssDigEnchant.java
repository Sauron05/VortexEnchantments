package com.vortexrpg.enchantments.enchant.impl.shovel;

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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Abyss Dig: Insta-mine soft blocks + auto-create 5-deep shaft collecting all drops. */
public class AbyssDigEnchant extends VortexEnchant {

    public AbyssDigEnchant() { super("abyss_dig", "Abyss Dig", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(player)) return;
        int depth = cfgi("depth", 3 + level * 2);
        int radius = cfgi("radius", level);
        Block center = event.getBlock();
        Location loc = center.getLocation().add(0.5, 0.5, 0.5);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 0; y >= -depth; y--) {
                    Block b = center.getRelative(x, y, z);
                    if (b.equals(center)) continue;
                    if (b.getType() == Material.BEDROCK || b.getType().isAir()) continue;
                    if (isSoftBlock(b.getType())) {
                        for (ItemStack drop : b.getDrops(player.getInventory().getItemInMainHand())) {
                            // Add directly to inventory, overflow drops naturally
                            var leftover = player.getInventory().addItem(drop);
                            leftover.values().forEach(item ->
                                    player.getWorld().dropItemNaturally(player.getLocation(), item));
                        }
                        b.setType(Material.AIR);
                    }
                }
            }
        }
        SoundUtil.play(loc, Sound.ENTITY_WARDEN_DIG, 1.0f, 0.8f);
        ParticleUtil.burst(loc.clone().add(0, -depth / 2.0, 0), Particle.PORTAL, 30, 1.5);
        setCooldownFromConfig(player, "cooldown", 12);
    }

    private boolean isSoftBlock(Material mat) {
        return mat == Material.DIRT || mat == Material.GRASS_BLOCK || mat == Material.SAND
                || mat == Material.RED_SAND || mat == Material.GRAVEL || mat == Material.CLAY
                || mat == Material.SOUL_SAND || mat == Material.SOUL_SOIL || mat == Material.MUD
                || mat == Material.SNOW_BLOCK || mat == Material.COARSE_DIRT || mat == Material.PODZOL
                || mat == Material.MYCELIUM || mat == Material.ROOTED_DIRT || mat == Material.FARMLAND
                || mat == Material.DIRT_PATH || mat == Material.SNOW || mat == Material.MUDDY_MANGROVE_ROOTS;
    }

    @Override public String getDescription() { return "Create deep shafts, collecting all drops."; }
    @Override public String getDescription(int level) {
        return "§7Dig §6" + (3 + level * 2) + "§7-deep shaft + auto-collect. Radius: §e" + level + "§7."; }
}
