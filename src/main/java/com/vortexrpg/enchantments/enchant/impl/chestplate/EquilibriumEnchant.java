package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Equilibrium: After taking damage, next attack deals +8/12/16% extra damage. */
public class EquilibriumEnchant extends VortexEnchant {
    private static final double[] BONUS = {0.08, 0.12, 0.16};

    public EquilibriumEnchant() { super("equilibrium", "Equilibrium", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "equilibrium_ready", 1);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (plugin.getPlayerDataManager().getInt(player.getUniqueId(), "equilibrium_ready") == 1) {
            double bonus = cfg("bonus", BONUS[level-1]);
            event.setDamage(event.getDamage() * (1.0 + bonus));
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "equilibrium_ready", 0);
        }
    }

    @Override public String getDescription() { return "Being hit empowers your next strike."; }
    @Override public String getDescription(int level) {
        return "§7After taking damage: next attack deals §a+" + (int)(BONUS[level-1]*100) + "§a%§7."; }
}
