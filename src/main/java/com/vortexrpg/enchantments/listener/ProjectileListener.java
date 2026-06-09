package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantManager;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class ProjectileListener implements Listener {

    private final VortexEnchantments plugin;
    private final EnchantManager manager;

    public ProjectileListener(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();

        // Resolve shooter from metadata
        if (proj.getMetadata("ve_shooter").isEmpty()) return;
        String uuidStr = proj.getMetadata("ve_shooter").get(0).asString();
        Player shooter;
        try {
            shooter = plugin.getServer().getPlayer(UUID.fromString(uuidStr));
        } catch (IllegalArgumentException e) {
            return;
        }
        if (shooter == null) return;

        // Forward to arrow-hit-block enchant hooks (block hits only — entity hits handled by EntityDamageByEntity)
        if (event.getHitBlock() != null) {
            ItemStack weapon = shooter.getInventory().getItemInMainHand();
            if (ItemUtil.isAir(weapon)) return;
            Map<VortexEnchant, Integer> enchants = manager.getEnchants(weapon);
            for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
                if (e.getKey().isEnabled()) {
                    e.getKey().onArrowHitBlock(event, shooter, e.getValue());
                }
            }
        }
    }
}
