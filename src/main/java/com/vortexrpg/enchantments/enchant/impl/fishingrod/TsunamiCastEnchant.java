package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** TsunamiCast: On catch, pushes all entities away from bobber. */
public class TsunamiCastEnchant extends VortexEnchant {

    public TsunamiCastEnchant() { super("tsunami_cast", "Tsunami Cast", EnchantRarity.EPIC, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        double radius = cfgd("radius", 4.0 + level);
        double force = cfgd("force", 0.6 + level * 0.2);
        event.getHook().getWorld().spawnParticle(Particle.SPLASH, event.getHook().getLocation(), 50, 2, 1, 2, 0.2);
        event.getHook().getWorld().playSound(event.getHook().getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1.5f, 0.8f);
        for (Entity e : event.getHook().getWorld().getNearbyEntities(event.getHook().getLocation(), radius, radius, radius)) {
            if (e == player || !(e instanceof LivingEntity le)) continue;
            Vector push = le.getLocation().toVector().subtract(event.getHook().getLocation().toVector()).normalize().multiply(force);
            push.setY(0.4);
            le.setVelocity(le.getVelocity().add(push));
        }
    }

    @Override public String getDescription() { return "Catching sends a wave from the bobber."; }
    @Override public String getDescription(int level) {
        return "§7On catch, §bwave§7 pushes entities away from bobber in §e" + (int)(4.0 + level) + "§7-block radius."; }
}
