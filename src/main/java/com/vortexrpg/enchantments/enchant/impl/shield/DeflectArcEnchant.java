package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** DeflectArc: Blocked projectiles are redirected at a random nearby enemy. */
public class DeflectArcEnchant extends VortexEnchant {
    public DeflectArcEnchant() { super("deflect_arc", "Deflect Arc", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled() || !player.isBlocking()) return;
        if (!(event.getDamager() instanceof Projectile)) return;
        int r = cfgi("radius", 12);
        var nearby = player.getWorld().getNearbyLivingEntities(player.getLocation(), r, r, r,
            e -> !(e instanceof Player)).stream().toList();
        if (nearby.isEmpty()) return;
        var target = nearby.get((int)(Math.random() * nearby.size()));
        double damage = event.getDamage() * (0.5 + level * 0.1);
        target.damage(damage, player);
        event.setDamage(event.getDamage() * 0.3);
    }

    @Override public String getDescription() { return "Blocked arrows ricochet to nearby enemies."; }
    @Override public String getDescription(int level) {
        return "§7Blocked projectiles ricochet to a random nearby mob."; }
}
