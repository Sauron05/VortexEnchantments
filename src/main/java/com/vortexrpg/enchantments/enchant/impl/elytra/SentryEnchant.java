package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Sentry — Elytra (Rare, Max 3)
 * While gliding, projectiles that would hit you are deflected by 50/65/80%.
 */
public class SentryEnchant extends VortexEnchant {

    public SentryEnchant() {
        super("sentry", "Sentry", "elytra");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] pct = {50, 65, 80};
        return "While gliding, §a" + pct[level - 1] + "%§7 of projectile damage is deflected.";
    }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!player.isGliding()) return;
        Entity damager = event.getDamager();
        if (!(damager instanceof Arrow)) return;
        double[] reductions = {0.50, 0.65, 0.80};
        double reduction = cfgd("deflect_reduction", reductions[level - 1]);
        event.setDamage(event.getDamage() * (1.0 - reduction));
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0, 1, 0), 4, 0.2, 0.2, 0.2, 0.05);
    }
}
