package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.List;

/** Piranha: On catch, chance to damage nearby water mobs. */
public class PiranhaEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.15, 0.20, 0.25};

    public PiranhaEnchant() { super("piranha", "Piranha", EnchantRarity.RARE, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (Math.random() >= cfgd("chance", CHANCE[level - 1])) return;
        double radius = cfgd("radius", 5.0);
        double damage = cfgd("damage", 2.0 + level);
        for (Entity e : event.getHook().getWorld().getNearbyEntities(event.getHook().getLocation(), radius, radius, radius)) {
            if (e == player || !(e instanceof LivingEntity le)) continue;
            if (le instanceof Player) continue;
            le.damage(damage, player);
        }
    }

    @Override public String getDescription() { return "Catching triggers piranha attack on nearby mobs."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level - 1] * 100) + "%§7 chance: nearby mobs take §c" + (int)(2.0 + level) + "§7 damage on catch."; }
}
