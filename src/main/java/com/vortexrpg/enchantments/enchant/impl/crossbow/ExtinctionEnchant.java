package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Extinction: Kill triggers a chain — if another entity dies within 3s, the chain
 * continues with +50% damage per link. The extinction event cascades.
 */
public class ExtinctionEnchant extends VortexEnchant {

    private static final String META_CHAIN = "vortex_extinction_chain";
    private static final String META_CHAIN_TIME = "vortex_extinction_time";

    public ExtinctionEnchant() {
        super("extinction", "Extinction", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int chainCount = 0;
        if (shooter.hasMetadata(META_CHAIN)) {
            long lastChainTime = shooter.getMetadata(META_CHAIN_TIME).getFirst().asLong();
            int window = cfgi("chain_window", 3) * 1000;
            if (System.currentTimeMillis() - lastChainTime < window) {
                chainCount = shooter.getMetadata(META_CHAIN).getFirst().asInt();
            }
        }

        double bonusPerChain = cfgd("bonus_per_chain", 0.5);
        double bonus = 1.0 + (chainCount * bonusPerChain);
        event.setDamage(event.getDamage() * bonus);

        if (chainCount > 0) {
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SOUL_FIRE_FLAME, 10 + chainCount * 5, 0.5);
            SoundUtil.play(victim.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.5f, 1.2f + chainCount * 0.1f);
        }

        double afterHealth = victim.getHealth() - event.getFinalDamage();
        if (afterHealth <= 0) {
            shooter.setMetadata(META_CHAIN, new FixedMetadataValue(plugin, chainCount + 1));
            shooter.setMetadata(META_CHAIN_TIME, new FixedMetadataValue(plugin, System.currentTimeMillis()));

            ParticleUtil.burst(victim.getLocation(), Particle.SCULK_SOUL, 15, 1.0);

            int finalChain = chainCount + 1;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (shooter.hasMetadata(META_CHAIN)) {
                        int current = shooter.getMetadata(META_CHAIN).getFirst().asInt();
                        if (current == finalChain) {
                            shooter.removeMetadata(META_CHAIN, plugin);
                            shooter.removeMetadata(META_CHAIN_TIME, plugin);
                        }
                    }
                }
            }.runTaskLater(plugin, cfgi("chain_window", 3) * 20L);
        }
    }

    @Override
    public String getDescription(int level) {
        return "§7Kill §4§lCHAINS §7— each kill within 3s adds §c+50% §7damage. Unstoppable cascade.";
    }
}
