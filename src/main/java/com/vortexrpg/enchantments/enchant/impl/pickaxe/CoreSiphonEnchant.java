package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Core Siphon: Every N blocks, massive radius explosion yielding all drops. */
public class CoreSiphonEnchant extends VortexEnchant {
    public CoreSiphonEnchant() { super("core_siphon", "Core Siphon", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        int threshold = cfgi("threshold", 60 - level * 10);
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "core_siphon_count") + 1;
        if (count >= threshold) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "core_siphon_count", 0);
            int radius = cfgi("radius", 4);
            Block center = event.getBlock();
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x * x + y * y + z * z > radius * radius) continue;
                        Block b = center.getRelative(x, y, z);
                        if (b.getType() != Material.BEDROCK && !b.getType().isAir()) {
                            b.breakNaturally(player.getInventory().getItemInMainHand());
                        }
                    }
                }
            }
            ParticleUtil.burst(center.getLocation().add(0.5, 0.5, 0.5), Particle.EXPLOSION, 10, 3.0);
            SoundUtil.play(center.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
        } else {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "core_siphon_count", count);
        }
    }

    @Override public String getDescription() { return "Build up energy from mining, release devastating explosion."; }
    @Override public String getDescription(int level) {
        return "§7Every §e" + (60 - level * 10) + " blocks§7: massive sphere explosion with all drops."; }
}
