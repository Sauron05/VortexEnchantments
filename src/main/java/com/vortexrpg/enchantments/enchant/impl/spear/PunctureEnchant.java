package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Puncture: Consecutive hits on the same target stack +8/12/15% damage.
 * Switching targets resets the chain.
 */
public class PunctureEnchant extends VortexEnchant {

    private static final String TARGET_KEY = "puncture_target";
    private static final String STACK_KEY = "puncture_stacks";

    public PunctureEnchant() {
        super("puncture", "Puncture", EnchantRarity.COMMON, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        var pdm = plugin.getPlayerDataManager();
        int lastTarget = pdm.getInt(attacker.getUniqueId(), TARGET_KEY);
        int victimHash = victim.getUniqueId().hashCode();

        int stacks;
        if (lastTarget == victimHash) {
            stacks = Math.min(pdm.getInt(attacker.getUniqueId(), STACK_KEY) + 1, 5);
        } else {
            stacks = 1;
        }
        pdm.setInt(attacker.getUniqueId(), TARGET_KEY, victimHash);
        pdm.setInt(attacker.getUniqueId(), STACK_KEY, stacks);

        double bonusPer = cfgd("bonus_per_stack", 0.05 + level * 0.03);
        event.setDamage(event.getDamage() * (1.0 + stacks * bonusPer));

        if (stacks >= 3) {
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 8, 0.3);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.03) * 100);
        return "§7Consecutive hits stack §c+" + pct + "% §7damage (max §e5§7 stacks).";
    }
}
