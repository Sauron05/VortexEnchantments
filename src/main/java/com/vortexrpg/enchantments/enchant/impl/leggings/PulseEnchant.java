package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Pulse: After 5/4/3 consecutive hits on same target, deal an extra burst of flat 2/3/4 damage. */
public class PulseEnchant extends VortexEnchant {
    private static final int[] THRESHOLD = {5, 4, 3};
    private static final double[] BURST = {2.0, 3.0, 4.0};

    public PulseEnchant() { super("pulse", "Pulse", EnchantRarity.EPIC, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onAttack(org.bukkit.event.entity.EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity target, int level) {
        if (!isEnabled()) return;
        int hits = plugin.getPlayerDataManager().incrementPulseHitCounter(player.getUniqueId(), target.getEntityId());
        int threshold = cfgi("threshold", THRESHOLD[level-1]);
        if (hits >= threshold) {
            plugin.getPlayerDataManager().resetPulseCounter(player.getUniqueId(), target.getEntityId());
            double burst = cfg("burst", BURST[level-1]);
            event.setDamage(event.getDamage() + burst);
            com.vortexrpg.enchantments.util.ParticleUtil.burst(target.getLocation(), org.bukkit.Particle.CRIT, 12, 0.4f);
        }
    }

    @Override public String getDescription() { return "Consecutive hits trigger a burst of damage."; }
    @Override public String getDescription(int level) {
        return "§7Every §a" + THRESHOLD[level-1] + "§7 hits: §a+" + BURST[level-1] + "§7 burst damage."; }
}
