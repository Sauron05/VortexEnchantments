package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * BucklerCharge — Shield (Rare, Max 3)
 * After blocking 3/2/1 consecutive hits, the next melee attack deals +30/40/50% bonus damage.
 */
public class BucklerChargeEnchant extends VortexEnchant {

    public BucklerChargeEnchant() {
        super("buckler_charge", "BucklerCharge", "shield");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] req = {3, 2, 1};
        int[] bonus = {30, 40, 50};
        return "Block §e" + req[level - 1] + "§7 hits in a row to empower your next attack with §a+" + bonus[level - 1] + "%§7 damage.";
    }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!player.isBlocking()) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "buckler_blocks", 0);
            return;
        }
        int[] required = {3, 2, 1};
        int req = cfgi("required_blocks", required[level - 1]);
        int current = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "buckler_blocks", 0) + 1;
        if (current >= req) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "buckler_charged", 1);
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "buckler_blocks", 0);
        } else {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "buckler_blocks", current);
        }
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, int level) {
        int charged = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "buckler_charged", 0);
        if (charged == 0) return;
        double[] bonuses = {0.30, 0.40, 0.50};
        double bonus = cfgd("bonus", bonuses[level - 1]);
        event.setDamage(event.getDamage() * (1 + bonus));
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "buckler_charged", 0);
    }
}
