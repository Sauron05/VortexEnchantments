package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Ruination: Left-click block in combat: shatters it and same-type blocks in 1/2/3 block radius.
 * Only works if player attacked/was attacked in last 10 seconds.
 */
public class RuinationEnchant extends VortexEnchant {

    private static final int[] RADIUS = {1, 2, 3};

    public RuinationEnchant() {
        super("ruination", "Ruination", EnchantRarity.RARE, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, ItemStack item, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(player)) return;

        long combatWindow = (long)(cfg("combat_window", 10.0) * 1000);
        long lastCombat = Math.max(
            plugin.getPlayerDataManager().getLong(player.getUniqueId(), "last_attack_time"),
            plugin.getPlayerDataManager().getLong(player.getUniqueId(), "last_damaged_time")
        );
        if (System.currentTimeMillis() - lastCombat > combatWindow) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null) return;

        int radius = cfgi("radius", RADIUS[level - 1]);
        Material targetType = clicked.getType();
        Location center = clicked.getLocation();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block b = center.getWorld().getBlockAt(
                        center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z
                    );
                    if (b.getType() == targetType) {
                        b.breakNaturally(item);
                    }
                }
            }
        }
        setCooldownFromConfig(player, "cooldown_seconds", 3.0);
    }

    @Override
    public String getDescription() { return "Left-click blocks in combat to shatter matching blocks nearby."; }

    @Override
    public String getDescription(int level) {
        return "§7In combat: left-click block to shatter matching blocks in §e" + RADIUS[level-1] + "§7-block radius.";
    }
}
