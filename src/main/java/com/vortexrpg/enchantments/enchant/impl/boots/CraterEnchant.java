package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Crater: Landing from 5+ block fall damages nearby enemies (damage = fall height / 3). */
@SuppressWarnings("deprecation")
public class CraterEnchant extends VortexEnchant {
    public CraterEnchant() { super("crater", "Crater", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        // Fall damage handled in EnchantListener for EntityDamageEvent (FALL cause)
        // Stub — actual logic wired in EnchantListener
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isOnGround()) {
            plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "crater_start_y",
                Math.max(plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "crater_start_y"), player.getLocation().getY()));
        } else {
            double startY = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "crater_start_y");
            double dropDist = startY - player.getLocation().getY();
            if (dropDist >= 5) {
                double dmg = dropDist / 3.0 * level;
                int r = cfgi("radius", 3 + level);
                player.getWorld().getNearbyLivingEntities(player.getLocation(), r, r, r,
                    e -> !(e instanceof Player)).forEach(e -> e.damage(dmg, player));
                com.vortexrpg.enchantments.util.ParticleUtil.ring(player.getLocation(), org.bukkit.Particle.CRIT, 20, r);
            }
            plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "crater_start_y", 0);
        }
    }

    @Override public String getDescription() { return "Landing from height damages nearby enemies."; }
    @Override public String getDescription(int level) {
        return "§7Fall 5+ blocks: deal area damage on landing (§a" + level + "x§7 power)."; }
}
