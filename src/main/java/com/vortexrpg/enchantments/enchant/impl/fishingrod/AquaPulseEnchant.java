package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.List;

/** AquaPulse: On catch, deal damage to hostile mobs near the bobber. */
public class AquaPulseEnchant extends VortexEnchant {
    private static final double[] DMG = {3.0, 4.0, 5.0};

    public AquaPulseEnchant() { super("aqua_pulse", "Aqua Pulse", EnchantRarity.RARE, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        double radius = cfgd("radius", 5.0);
        double damage = cfgd("damage", DMG[level - 1]);
        event.getHook().getWorld().spawnParticle(Particle.SPLASH, event.getHook().getLocation(), 30, 1, 0.5, 1, 0.1);
        for (Entity e : event.getHook().getWorld().getNearbyEntities(event.getHook().getLocation(), radius, radius, radius)) {
            if (e == player || !(e instanceof LivingEntity le)) continue;
            if (le instanceof Player) continue;
            le.damage(damage, player);
        }
    }

    @Override public String getDescription() { return "AoE damage at bobber on catch."; }
    @Override public String getDescription(int level) {
        return "§7On catch, §c" + (int) DMG[level - 1] + "§7 damage to mobs within §e5§7 blocks of bobber."; }
}
