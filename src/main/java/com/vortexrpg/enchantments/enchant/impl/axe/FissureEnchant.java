package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Fissure: Left-click ground: create 1×3/1×5/1×7 crack in facing direction.
 * Entities above fall in. Restored after 4s.
 */
public class FissureEnchant extends VortexEnchant {

    private static final int[] CRACK_LENGTHS = {3, 5, 7};

    public FissureEnchant() {
        super("fissure", "Fissure", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, ItemStack item, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(player)) return;
        if (event.getClickedBlock() == null) return;

        int length = cfgi("crack_length", CRACK_LENGTHS[level - 1]);
        double fallDamage = cfg("fall_damage", level * 2.0);

        Location playerLoc = player.getLocation();
        var dir = playerLoc.getDirection();
        dir.setY(0);
        dir.normalize();

        List<Block> removed = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Location checkLoc = playerLoc.clone().add(dir.clone().multiply(i + 1));
            Block ground = checkLoc.getWorld().getBlockAt(checkLoc.getBlockX(), checkLoc.getBlockY() - 1, checkLoc.getBlockZ());
            if (ground.getType() != Material.AIR && ground.getType() != Material.VOID_AIR) {
                var savedType = ground.getType();
                var savedData = ground.getBlockData();
                ground.setType(Material.AIR);
                removed.add(ground);

                // Damage entities in the crack
                for (LivingEntity e : MathUtil.getNearbyLiving(ground.getLocation(), 1.0)) {
                    if (!e.equals(player)) e.damage(fallDamage, player);
                }

                long restoreSecs = cfgi("restore_seconds", 4);
                final Block fb = ground;
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    fb.setType(savedType);
                    fb.setBlockData(savedData);
                }, restoreSecs * 20L);
            }
        }
        setCooldownFromConfig(player, "cooldown_seconds", 4.0);
    }

    @Override
    public String getDescription() { return "Left-click ground to crack it, dropping enemies into a pit."; }

    @Override
    public String getDescription(int level) {
        return "§7Left-click: §c1×" + CRACK_LENGTHS[level-1] + "§7 crack deals §c" + (level*2) + "♥§7 fall damage. Restored after §e4s§7.";
    }
}
