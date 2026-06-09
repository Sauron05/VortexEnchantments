package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Wrangler — Fishing Rod (Epic, Max 3)
 * Hooking an entity (non-fish) with the rod also applies Slowness and pulls them closer significantly.
 */
public class WranglerEnchant extends VortexEnchant {

    public WranglerEnchant() {
        super("wrangler", "Wrangler", "fishingrod");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        return "Hooking an entity with the rod slows them and pulls them §e" + (level * 3) + " blocks§7 toward you.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) return;
        Entity caught = event.getCaught();
        if (!(caught instanceof LivingEntity le)) return;

        // Apply slowness
        int dur = cfgi("slow_duration_ticks", 60);
        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, dur, level, false, true, true));

        // Pull toward player
        double pullForce = cfgd("pull_force", 1.0 * level);
        org.bukkit.util.Vector dir = player.getLocation().toVector()
                .subtract(le.getLocation().toVector()).normalize().multiply(pullForce);
        dir.setY(0.3);
        le.setVelocity(dir);
    }
}
