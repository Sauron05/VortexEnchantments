package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;


/**
 * Verdict: Bonus damage proportional to how many different players attacked the target in the last 10s.
 * +15%/20%/25% per unique attacker.
 */
public class VerdictEnchant extends VortexEnchant {

    private static final double[] BONUS_PER_ATTACKER = {0.15, 0.20, 0.25};

    public VerdictEnchant() {
        super("verdict", "Verdict", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        long windowMs = (long)(cfg("window_seconds", 10.0) * 1000);
        int maxAttackers = cfgi("max_attackers", 5);

        var pdm = plugin.getPlayerDataManager();
        pdm.recordEntityAttacker(victim.getUniqueId(), attacker.getUniqueId());
        int uniqueAttackers = Math.min(pdm.getRecentAttackerCount(victim.getUniqueId(), windowMs), maxAttackers);

        double bonus = uniqueAttackers * cfg("bonus_per_attacker", BONUS_PER_ATTACKER[level - 1]);
        if (bonus > 0) {
            event.setDamage(event.getDamage() * (1.0 + bonus));
        }
    }

    @Override
    public String getDescription() { return "More attackers on the target = more bonus damage."; }

    @Override
    public String getDescription(int level) {
        return "§7+§e" + (int)(BONUS_PER_ATTACKER[level-1]*100) + "%§7 damage per unique attacker on target within §e10s§7.";
    }
}
