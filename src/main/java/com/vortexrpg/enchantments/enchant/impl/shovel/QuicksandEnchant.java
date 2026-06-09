package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

/** Quicksand: Right-click sand/dirt to create a 3×3 sinking trap. */
public class QuicksandEnchant extends VortexEnchant {
    private static final Set<Material> VALID = Set.of(Material.SAND, Material.DIRT, Material.GRAVEL);

    public QuicksandEnchant() { super("quicksand", "Quicksand", EnchantRarity.RARE, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getClickedBlock() == null) return;
        if (!VALID.contains(event.getClickedBlock().getType())) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, cfgi("cooldown", 10));
        Block center = event.getClickedBlock();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block above = center.getRelative(x, 1, z);
                if (above.getType() == Material.AIR) {
                    above.setType(Material.SAND);
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        if (above.getType() == Material.SAND) above.setType(Material.AIR);
                    }, 60L);
                }
            }
        }
        // Apply Slowness to entities in trap area
        for (org.bukkit.entity.LivingEntity le : com.vortexrpg.enchantments.util.MathUtil.getNearbyLiving(center.getLocation(), 2)) {
            if (le.equals(player)) continue;
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
            le.setVelocity(le.getVelocity().add(new Vector(0, -0.3, 0)));
        }
    }

    @Override public String getDescription() { return "Create a quicksand trap on right-click."; }
    @Override public String getDescription(int level) {
        return "§7Right-click sand/dirt: §e3×3§7 quicksand trap. Entities §bSlow§7 + sink."; }
}
