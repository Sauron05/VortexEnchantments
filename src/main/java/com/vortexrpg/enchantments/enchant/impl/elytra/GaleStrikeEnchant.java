package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/** GaleStrike: AoE damage on high-speed landing. */
public class GaleStrikeEnchant extends VortexEnchant {
    private static final double[] DMG = {4.0, 6.0, 8.0};

    public GaleStrikeEnchant() { super("gale_strike", "Gale Strike", EnchantRarity.RARE, 3, List.of(ItemTarget.ELYTRA)); }

    @SuppressWarnings("deprecation")
    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        boolean wasGliding = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "gs_gl", 0) == 1;
        boolean isGliding = player.isGliding();
        if (isGliding) {
            plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "gs_speed", player.getVelocity().length());
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "gs_gl", isGliding ? 1 : 0);
        if (wasGliding && !isGliding && player.isOnGround()) {
            double speed = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "gs_speed", 0);
            if (speed >= cfgd("min_speed", 1.0)) {
                double damage = cfgd("damage", DMG[level - 1]);
                double radius = cfgd("radius", 4.0);
                for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
                    if (e == player || !(e instanceof LivingEntity le)) continue;
                    le.damage(damage, player);
                }
                player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, player.getLocation(), 1);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.2f);
            }
        }
    }

    @Override public String getDescription() { return "Shockwave on high-speed landing."; }
    @Override public String getDescription(int level) {
        return "§7High-speed landing deals §c" + (int) DMG[level - 1] + "§7 AoE damage in §e4 blocks§7."; }
}
