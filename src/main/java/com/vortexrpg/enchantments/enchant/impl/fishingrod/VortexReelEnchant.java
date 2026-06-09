package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** VortexReel: On cast, creates a whirlpool that pulls in items and entities. */
public class VortexReelEnchant extends VortexEnchant {

    public VortexReelEnchant() { super("vortex_reel", "Vortex Reel", EnchantRarity.EPIC, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.FISHING) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, cfgi("cooldown", Math.max(5, 15 - level * 3)));
        double radius = cfgd("radius", 3.0 + level);
        event.getHook().getWorld().spawnParticle(Particle.SPLASH, event.getHook().getLocation(), 40, radius, 0.5, radius, 0.1);
        for (Entity e : event.getHook().getWorld().getNearbyEntities(event.getHook().getLocation(), radius, radius, radius)) {
            if (e == player) continue;
            Vector pull = event.getHook().getLocation().toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.3);
            if (e instanceof Item || e instanceof LivingEntity) {
                e.setVelocity(e.getVelocity().add(pull));
            }
        }
    }

    @Override public String getDescription() { return "Casting creates a whirlpool at the bobber."; }
    @Override public String getDescription(int level) {
        return "§7On cast, §bwhirlpool§7 pulls items and entities within §e" + (int)(3.0 + level) + "§7 blocks."; }
}
