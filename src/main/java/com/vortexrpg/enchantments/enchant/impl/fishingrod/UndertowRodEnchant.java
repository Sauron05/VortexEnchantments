package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * UndertowRod — Fishing Rod (Rare, Max 3)
 * When reeling in an entity, also drags nearby mobs toward the hook location.
 */
public class UndertowRodEnchant extends VortexEnchant {

    public UndertowRodEnchant() {
        super("undertow_rod", "Undertow", "fishingrod");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        double[] radii = {4, 6, 8};
        return "Reeling in creates an undertow pulling mobs within §e" + (int)radii[level - 1] + " blocks§7 toward the hook.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY
                && event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        double[] radii = {4.0, 6.0, 8.0};
        double radius = cfgd("pull_radius", radii[level - 1]);
        org.bukkit.Location hookLoc = event.getHook().getLocation();
        double pullForce = cfgd("pull_force", 0.6);

        for (org.bukkit.entity.Entity e : player.getWorld().getNearbyEntities(hookLoc, radius, radius, radius)) {
            if (e == player) continue;
            if (!(e instanceof org.bukkit.entity.LivingEntity)) continue;
            org.bukkit.util.Vector dir = hookLoc.toVector().subtract(e.getLocation().toVector()).normalize().multiply(pullForce);
            dir.setY(0.15);
            e.setVelocity(dir);
        }
    }
}
