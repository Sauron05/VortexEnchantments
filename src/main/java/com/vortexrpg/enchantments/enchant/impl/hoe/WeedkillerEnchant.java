package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Set;

/** Weedkiller: Right-click to clear non-crop vegetation in 3/4/5 block radius. */
public class WeedkillerEnchant extends VortexEnchant {
    private static final double[] RADIUS = {3, 4, 5};
    private static final Set<Material> VEGETATION = Set.of(
        Material.SHORT_GRASS, Material.TALL_GRASS, Material.FERN, Material.LARGE_FERN,
        Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.DEAD_BUSH,
        Material.DANDELION, Material.POPPY, Material.ALLIUM, Material.AZURE_BLUET
    );

    public WeedkillerEnchant() { super("weedkiller", "Weedkiller", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, 3);
        double radius = cfg("radius", RADIUS[level-1]);
        Location origin = player.getLocation();
        int r = (int) radius;
        for (int x = -r; x <= r; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -r; z <= r; z++) {
                    Block b = origin.clone().add(x, y, z).getBlock();
                    if (VEGETATION.contains(b.getType())) b.setType(Material.AIR);
                }
            }
        }
    }

    @Override public String getDescription() { return "Right-click to clear nearby vegetation."; }
    @Override public String getDescription(int level) {
        return "§7Right-click: clears vegetation in §a" + (int)RADIUS[level-1] + " block§7 radius."; }
}
