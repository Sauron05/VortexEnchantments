package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Field Clear: Right-click to harvest all mature crops in radius. */
public class FieldClearEnchant extends VortexEnchant {

    public FieldClearEnchant() { super("field_clear", "Field Clear", EnchantRarity.EPIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;
        int radius = cfgi("radius", 2 + level);
        Block center = player.getLocation().getBlock();
        int harvested = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block b = center.getRelative(x, 0, z);
                if (b.getBlockData() instanceof Ageable age && age.getAge() >= age.getMaximumAge()) {
                    for (var drop : b.getDrops(player.getInventory().getItemInMainHand())) {
                        var leftover = player.getInventory().addItem(drop);
                        leftover.values().forEach(item ->
                                player.getWorld().dropItemNaturally(player.getLocation(), item));
                    }
                    // Replant
                    var mat = b.getType();
                    b.setType(mat);
                    harvested++;
                }
            }
        }
        if (harvested > 0) {
            ParticleUtil.burst(player.getLocation().add(0, 1, 0), Particle.HAPPY_VILLAGER, 20, radius);
            setCooldownFromConfig(player, "cooldown", 10);
        }
    }

    @Override public String getDescription() { return "Right-click: harvest all crops in radius."; }
    @Override public String getDescription(int level) {
        return "§7Right-click: harvest crops in §e" + (2 + level) + "§7b radius + auto-collect."; }
}
