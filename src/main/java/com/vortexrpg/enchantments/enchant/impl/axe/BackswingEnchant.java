package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Backswing: Missing a swing sets a flag. Next hit within 2s deals +60%/80%/100%.
 * Miss detection: arm animation without damage event in 2 ticks.
 */
public class BackswingEnchant extends VortexEnchant {

    private static final double[] BONUS_DAMAGE = {0.60, 0.80, 1.00};

    public BackswingEnchant() {
        super("backswing", "Backswing", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        var pdm = plugin.getPlayerDataManager();

        if (pdm.isBackswingReady(attacker.getUniqueId())) {
            double bonus = cfg("bonus_damage_percent", BONUS_DAMAGE[level - 1]);
            event.setDamage(event.getDamage() * (1.0 + bonus));
            pdm.clearBackswing(attacker.getUniqueId());
        }
    }

    /** Called by CombatListener on arm-swing animation without corresponding damage. */
    public void onMissSwing(Player player, int level) {
        if (!isEnabled()) return;
        double windowSecs = cfg("window_seconds", 2.0);
        long expiry = System.currentTimeMillis() + (long)(windowSecs * 1000);
        plugin.getPlayerDataManager().setBackswingReady(player.getUniqueId(), expiry);
    }

    @Override
    public String getDescription() { return "Missing a swing boosts the next hit within 2 seconds."; }

    @Override
    public String getDescription(int level) {
        return "§7Miss: next hit within §e2s§7 deals §a+" + (int)(BONUS_DAMAGE[level-1]*100) + "%§7 bonus damage.";
    }
}
