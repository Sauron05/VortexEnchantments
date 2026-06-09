package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Embargo: After taking 3/2/1 hits without attacking, gain 20/25/30% DR until next attack. */
public class EmbargoEnchant extends VortexEnchant {
    private static final int[] REQUIRED_HITS = {3, 2, 1};
    private static final double[] DR = {0.20, 0.25, 0.30};

    public EmbargoEnchant() { super("embargo", "Embargo", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        int hits = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "embargo_hits") + 1;
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "embargo_hits", hits);
        int required = cfgi("required_hits", REQUIRED_HITS[level-1]);
        if (hits >= required) {
            double dr = cfg("dr", DR[level-1]);
            event.setDamage(event.getDamage() * (1.0 - dr));
        }
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity target, int level) {
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "embargo_hits", 0);
    }

    @Override public String getDescription() { return "Taking hits without attacking builds DR."; }
    @Override public String getDescription(int level) {
        return "§7After §a" + REQUIRED_HITS[level-1] + "§7 hits without attacking: §a" + (int)(DR[level-1]*100) + "§a%§7 DR."; }
}
