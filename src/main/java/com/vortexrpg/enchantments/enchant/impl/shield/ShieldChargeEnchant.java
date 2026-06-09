package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/** Shield Charge: Sprint+block charges forward; release for AoE knockback. */
public class ShieldChargeEnchant extends VortexEnchant {

    public ShieldChargeEnchant() { super("shield_charge", "Shield Charge", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isSprinting() || !player.isBlocking()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 5 != 0) return;
        String key = "shield_charge_ticks";
        int chargeTicks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), key) + 1;
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, chargeTicks);
        ParticleUtil.spawn(player.getLocation().add(0, 1, 0), Particle.CLOUD, 3, 0.3);
    }

    @Override
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isLeftClick()) return;
        if (isOnCooldown(player)) return;
        String key = "shield_charge_ticks";
        int chargeTicks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), key);
        if (chargeTicks < 4) return;
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, 0);
        double radius = cfg("radius", 3.0 + level);
        double damage = cfg("damage", 1.0 + level + Math.min(chargeTicks * 0.5, 5));
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.damage(damage, player);
            Vector push = e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(0.6).setY(0.3);
            e.setVelocity(push);
        }
        ParticleUtil.burst(player.getLocation(), Particle.EXPLOSION, 5, radius);
        setCooldownFromConfig(player, "cooldown", 8);
    }

    @Override public String getDescription() { return "Sprint+block charges; release for AoE."; }
    @Override public String getDescription(int level) {
        return "§7Sprint+block → release: §cAoE damage§7 + knockback in " + (int)(3 + level) + "b."; }
}
