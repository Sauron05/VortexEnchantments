package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** Infinite Aegis: Blocking generates an expanding shield dome that protects all inside. */
public class InfiniteAegisEnchant extends VortexEnchant {

    public InfiniteAegisEnchant() { super("infinite_aegis", "Infinite Aegis", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 10 != 0) return;
        String key = "infinite_aegis_radius";
        double maxRadius = cfg("max-radius", 4.0 + level * 2);
        double currentRadius = Math.min(
                plugin.getPlayerDataManager().getInt(player.getUniqueId(), key) * 0.5 + 1.0,
                maxRadius);
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), key,
                (int)(currentRadius * 2));
        ParticleUtil.drawCircle(player.getLocation().add(0, 1, 0), currentRadius, 30, Particle.END_ROD);
        // Apply protection to all inside
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), currentRadius)) {
            if (e instanceof Player ally) {
                plugin.getPlayerDataManager().setLong(ally.getUniqueId(), "aegis_dome_expiry",
                        System.currentTimeMillis() + 1500);
                plugin.getPlayerDataManager().setInt(ally.getUniqueId(), "aegis_dome_level", level);
            }
        }
    }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        long expiry = plugin.getPlayerDataManager().getLong(player.getUniqueId(), "aegis_dome_expiry", 0L);
        if (System.currentTimeMillis() > expiry) return;
        int domeLevel = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "aegis_dome_level");
        double reduction = cfg("dome-reduction", 0.2 + domeLevel * 0.1);
        event.setDamage(event.getDamage() * (1.0 - reduction));
    }

    @Override public String getDescription() { return "Blocking expands protective dome."; }
    @Override public String getDescription(int level) {
        return "§7Block: expanding §ddome§7 (max " + (int)(4 + level * 2) + "b) protects all inside."; }
}
