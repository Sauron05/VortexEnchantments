package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Retribution: Blocked damage charges counter-attack beam. */
public class RetributionEnchant extends VortexEnchant {

    public RetributionEnchant() { super("retribution", "Retribution", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        String key = "retribution_charge";
        double stored = plugin.getPlayerDataManager().getInt(player.getUniqueId(), key) + event.getDamage();
        double maxCharge = cfg("max-charge", 10.0 + level * 5);
        stored = Math.min(stored, maxCharge);
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, (int) stored);
    }

    @Override
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isLeftClick()) return;
        if (isOnCooldown(player)) return;
        String key = "retribution_charge";
        int charge = plugin.getPlayerDataManager().getInt(player.getUniqueId(), key);
        if (charge < 5) return;
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, 0);
        double damage = charge * cfg("damage-mult", 0.5 + level * 0.25);
        double range = cfg("range", 5.0 + level);
        Vector dir = player.getLocation().getDirection().normalize();
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), range)) {
            if (e.equals(player)) continue;
            var toE = e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            if (dir.angle(toE) < Math.PI / 4) {
                e.damage(damage, player);
            }
        }
        ParticleUtil.burst(player.getLocation().add(dir.multiply(2)).add(0, 1, 0), Particle.END_ROD, 20, 1.0);
        setCooldownFromConfig(player, "cooldown", 10);
    }

    @Override public String getDescription() { return "Blocked damage charges counter-beam."; }
    @Override public String getDescription(int level) {
        return "§7Store blocked damage → §6release beam§7. Max §c" + (int)(10 + level * 5) + "§7 charge."; }
}
