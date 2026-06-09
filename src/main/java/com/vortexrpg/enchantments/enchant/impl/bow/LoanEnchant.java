package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Loan: Steal 1/2/3 XP levels from target player. If no XP: deal +2/3/4♥ extra.
 */
public class LoanEnchant extends VortexEnchant {

    private static final int[] XP_STEAL = {1, 2, 3};
    private static final double[] NO_XP_DAMAGE = {4.0, 6.0, 8.0};

    public LoanEnchant() {
        super("loan", "Loan", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!(victim instanceof Player target)) return;

        int steal = cfgi("xp_steal_levels", XP_STEAL[level - 1]);
        if (target.getLevel() >= steal) {
            target.setLevel(target.getLevel() - steal);
            shooter.setLevel(shooter.getLevel() + steal);
            target.sendMessage("§6[Loan] §7Lost " + steal + " XP levels!");
        } else {
            event.setDamage(event.getDamage() + cfg("no_xp_bonus_damage", NO_XP_DAMAGE[level - 1]));
        }
    }

    @Override
    public String getDescription() { return "Steals XP levels from player targets. No XP? Extra damage instead."; }

    @Override
    public String getDescription(int level) {
        return "§7Steal §b" + XP_STEAL[level-1] + " XP levels§7 from target. If none: §c+" + (int)(NO_XP_DAMAGE[level-1]/2) + "♥§7 extra.";
    }
}
