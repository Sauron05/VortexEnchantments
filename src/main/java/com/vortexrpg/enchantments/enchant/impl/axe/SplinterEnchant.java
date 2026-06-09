package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionType;

import java.util.List;

/**
 * Splinter: 10%/15%/20% chance to spawn 3 AECs around target after 1s dealing 1♥ each.
 */
public class SplinterEnchant extends VortexEnchant {

    private static final double[] CHANCE = {10.0, 15.0, 20.0};

    public SplinterEnchant() {
        super("splinter", "Splinter", EnchantRarity.RARE, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double chance = cfg("chance_percent", CHANCE[level - 1]);
        if (!MathUtil.chance(chance)) return;

        double damage = cfg("splinter_damage", 2.0);
        long delay = cfgi("delay_ticks", 20);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!victim.isValid() || victim.isDead()) return;
            for (LivingEntity nearby : MathUtil.getNearbyLiving(victim.getLocation(), 2.0)) {
                if (!nearby.equals(attacker)) {
                    nearby.damage(damage, attacker);
                }
            }
            // AEC visual
            AreaEffectCloud cloud = victim.getWorld().spawn(victim.getLocation(), AreaEffectCloud.class);
            cloud.setRadius(1.5f);
            cloud.setDuration(20);
            cloud.setBasePotionType(PotionType.HARMING);
            cloud.setRadiusOnUse(0);
            cloud.setRadiusPerTick(0);
            plugin.getServer().getScheduler().runTaskLater(plugin, cloud::remove, 5L);
        }, delay);
    }

    @Override
    public String getDescription() { return "Hits may cause area-of-effect splinter explosions."; }

    @Override
    public String getDescription(int level) {
        return "§7" + (int)CHANCE[level-1] + "%§7 chance: §c3 splinters§7 deal §c1♥§7 each around target after §e1s§7.";
    }
}
