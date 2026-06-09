package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Pinion: Headshots (top 25% of hitbox) deal 2×/2.25×/2.5× damage.
 */
public class PinionEnchant extends VortexEnchant {

    private static final double[] HEADSHOT_MULT = {2.0, 2.25, 2.5};

    public PinionEnchant() {
        super("pinion", "Pinion", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof Arrow arrow)) return;

        double headshotZone = cfg("headshot_zone_percent", 0.25);
        if (MathUtil.isHeadshot(victim, arrow.getLocation(), headshotZone)) {
            event.setDamage(event.getDamage() * cfg("headshot_multiplier", HEADSHOT_MULT[level - 1]));
            shooter.sendMessage("§6[Pinion] §eHeadshot!");
        }
    }

    @Override
    public String getDescription() { return "Headshots deal massively increased damage."; }

    @Override
    public String getDescription(int level) {
        return "§7Headshots (top §e25%§7 of hitbox): §c×" + HEADSHOT_MULT[level-1] + "§7 damage!";
    }
}
