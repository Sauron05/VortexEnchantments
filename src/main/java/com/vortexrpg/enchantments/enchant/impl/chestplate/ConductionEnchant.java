package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Conduction: Hit while wet (in rain or water) to slows all nearby enemies for 2/3/4s (Mining Fatigue). */
public class ConductionEnchant extends VortexEnchant {
    private static final int[] DUR = {2, 3, 4};

    public ConductionEnchant() { super("conduction", "Conduction", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isInWaterOrRain()) return;
        int dur = cfgi("duration", DUR[level-1]);
        int r = cfgi("radius", 6);
        player.getWorld().getNearbyLivingEntities(player.getLocation(), r, r, r,
            e -> !(e instanceof Player)).forEach(e ->
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * dur, 1)));
    }

    @Override public String getDescription() { return "Wet hits create an electric shockwave slowing mobs."; }
    @Override public String getDescription(int level) {
        return "§7While wet + hit: nearby mobs §aSlowed§7 for §a" + DUR[level-1] + "s§7."; }
}
