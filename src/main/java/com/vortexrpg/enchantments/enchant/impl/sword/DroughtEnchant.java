package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

/**
 * Drought: Each hit drains 3/4/5 saturation from target player.
 * When saturation reaches 0: target takes +50% damage from ALL sources for 6s.
 */
public class DroughtEnchant extends VortexEnchant {

    private static final int[] SATURATION_DRAIN = {3, 4, 5};

    public DroughtEnchant() {
        super("drought", "Drought", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!(victim instanceof Player targetPlayer)) return;

        int drain = cfgi("saturation_drain", SATURATION_DRAIN[level - 1]);
        double ampPercent = cfg("damage_amp_percent", 50.0);
        long ampDurationMs = (long) (cfg("amp_duration_seconds", 6.0) * 1000);

        float currentSat = targetPlayer.getSaturation();
        float newSat = Math.max(0, currentSat - drain);
        targetPlayer.setSaturation(newSat);

        // Apply drought debuff if dehydrated
        if (targetPlayer.getSaturation() <= 0) {
            long expiry = System.currentTimeMillis() + ampDurationMs;
            targetPlayer.setMetadata("drought_expiry", new FixedMetadataValue(plugin, expiry));
            targetPlayer.sendMessage("§cYou are parched! Incoming damage increased!");
        }

        // Check if target is currently drought-amplified
        List<MetadataValue> meta = victim.getMetadata("drought_expiry");
        if (!meta.isEmpty()) {
            long expiry = meta.get(0).asLong();
            if (System.currentTimeMillis() < expiry) {
                event.setDamage(event.getDamage() * (1.0 + ampPercent / 100.0));
            } else {
                victim.removeMetadata("drought_expiry", plugin);
            }
        }

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.RAIN, 6, 0.3);
    }

    @Override
    public String getDescription() { return "Drains saturation on each hit. Dehydrated targets take more damage."; }

    @Override
    public String getDescription(int level) {
        int drain = SATURATION_DRAIN[level - 1];
        return "Drain §b" + drain + " §7saturation per hit. At 0 saturation: §c+50% §7dmg for 6s.";
    }
}
