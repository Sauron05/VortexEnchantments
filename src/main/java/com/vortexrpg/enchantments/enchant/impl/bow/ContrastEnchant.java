package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Contrast: Night = fire arrows. Day = ice arrows (Slowness II + freeze).
 */
public class ContrastEnchant extends VortexEnchant {

    public ContrastEnchant() {
        super("contrast", "Contrast", EnchantRarity.RARE, 1, List.of(ItemTarget.BOW));
    }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        if (!(event.getProjectile() instanceof Arrow arrow)) return;

        long worldTime = shooter.getWorld().getTime();
        int nightStart = cfgi("night_fire_ticks", 13000);
        int nightEnd = cfgi("night_end_ticks", 23000);

        if (worldTime >= nightStart && worldTime <= nightEnd) {
            // Night: fire arrows
            arrow.setFireTicks(60);
        } else {
            // Day: ice arrows - apply slowness on hit via metadata
            arrow.setMetadata("contrast_ice", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
        }
    }

    @Override
    public void onArrowHitEntity(org.bukkit.event.entity.EntityDamageByEntityEvent event,
                                 Player shooter, org.bukkit.entity.LivingEntity victim, int level) {
        if (event.getDamager().hasMetadata("contrast_ice")) {
            int slownessDuration = cfgi("day_slowness_duration", 80);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slownessDuration, 1, false, true));
            victim.setFreezeTicks(cfgi("day_freeze_ticks", 60));
        }
    }

    @Override
    public String getDescription() { return "Night: fire arrows. Day: ice arrows that slow and freeze."; }

    @Override
    public String getDescription(int level) {
        return "§7Night: §carrow sets ablaze§7. Day: §barrow chills§7 target (Slowness II + freeze).";
    }
}
