package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Random;

/**
 * Proxy: Arrows passing within 2/3/4 blocks of an ally deal +20%/25%/30% damage + copy 1 ally effect.
 */
public class ProxyEnchant extends VortexEnchant {

    private static final double[] PROXY_RADIUS = {2.0, 3.0, 4.0};
    private static final double[] DAMAGE_BONUS = {0.20, 0.25, 0.30};

    public ProxyEnchant() {
        super("proxy", "Proxy", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof org.bukkit.entity.Arrow arrow)) return;

        double radius = cfg("proxy_radius", PROXY_RADIUS[level - 1]);
        for (org.bukkit.entity.Entity nearby : arrow.getWorld().getNearbyEntities(arrow.getLocation(), radius, radius, radius)) {
            if (!(nearby instanceof Player ally) || ally.equals(shooter) || ally.equals(victim)) continue;
            event.setDamage(event.getDamage() * (1.0 + cfg("damage_bonus", DAMAGE_BONUS[level - 1])));

            if (cfgb("copy_effects", true)) {
                var effects = new java.util.ArrayList<>(ally.getActivePotionEffects());
                if (!effects.isEmpty()) {
                    PotionEffect ef = effects.get(new Random().nextInt(effects.size()));
                    shooter.addPotionEffect(ef);
                }
            }
            break;
        }
    }

    @Override
    public String getDescription() { return "Arrows passing near allies deal more damage and copy effects."; }

    @Override
    public String getDescription(int level) {
        return "§7Arrow passes §e" + PROXY_RADIUS[level-1] + "§7 blocks from ally: §a+" + (int)(DAMAGE_BONUS[level-1]*100) + "%§7 + copy 1 effect.";
    }
}
