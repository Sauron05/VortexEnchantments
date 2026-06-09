package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

/**
 * LeviathanHook — Fishing Rod (Legendary, Max 1)
 * On catching an entity, deals massive damage and lifts it high into the air.
 */
public class LeviathanHookEnchant extends VortexEnchant {

    public LeviathanHookEnchant() {
        super("leviathan_hook", "LeviathanHook", "fishingrod");
    }

    @Override
    public String getTier() { return "LEGENDARY"; }

    @Override
    public int getMaxLevel() { return 1; }

    @Override
    public String getDescription(int level) {
        return "§dHooking an entity deals §c12 damage§d and launches them skyward.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) return;
        Entity caught = event.getCaught();
        if (!(caught instanceof LivingEntity le)) return;

        double damage = cfgd("damage", 12.0);
        double liftForce = cfgd("lift_force", 2.0);
        le.damage(damage, player);
        le.setVelocity(new Vector(0, liftForce, 0));
        player.getWorld().spawnParticle(Particle.SPLASH, le.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 0.9f, 0.8f);
        player.sendMessage("§d[LeviathanHook] §5The deep answers your call!");
    }
}
