package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** World Guardian: While blocking, all nearby allies take 50% less damage. */
public class WorldGuardianEnchant extends VortexEnchant {

    public WorldGuardianEnchant() { super("world_guardian", "World Guardian", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        // Mark allies as protected via player data
        double radius = cfg("radius", 8.0 + level * 2);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e instanceof Player ally && !ally.equals(player)) {
                plugin.getPlayerDataManager().setLong(ally.getUniqueId(), "world_guardian_expiry",
                        System.currentTimeMillis() + 1500);
                plugin.getPlayerDataManager().setInt(ally.getUniqueId(), "world_guardian_level", level);
            }
        }
    }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        // This applies TO the protected allies
        if (!isEnabled()) return;
        long expiry = plugin.getPlayerDataManager().getLong(player.getUniqueId(), "world_guardian_expiry", 0L);
        if (System.currentTimeMillis() > expiry) return;
        int guardLevel = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "world_guardian_level");
        double reduction = cfg("ally-reduction", 0.3 + guardLevel * 0.1);
        event.setDamage(event.getDamage() * (1.0 - reduction));
    }

    @Override public String getDescription() { return "While blocking: allies take less damage."; }
    @Override public String getDescription(int level) {
        return "§7Block: allies in §e" + (int)(8 + level * 2) + "b§7 take §a" + (int)((0.3 + level * 0.1) * 100) + "%§7 less damage."; }
}
