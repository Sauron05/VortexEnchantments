package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Reservoir: Passively store up to 5/8/10 hearts; drain when below 30% HP. */
public class ReservoirEnchant extends VortexEnchant {
    private static final double[] MAX_HP = {10.0, 16.0, 20.0};
    private static final double THRESHOLD = 0.30;

    public ReservoirEnchant() { super("reservoir", "Reservoir", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double maxReservoir = cfg("max_reservoir", MAX_HP[level-1]);
        double stored = plugin.getPlayerDataManager().getReservoirStored(player.getUniqueId());
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        // Charge reservoir when HP > 80%
        if (player.getHealth() > maxHp * 0.80 && stored < maxReservoir) {
            plugin.getPlayerDataManager().addReservoirStored(player.getUniqueId(), 0.1);
        }
        // Drain reservoir when HP < 30%
        if (player.getHealth() < maxHp * THRESHOLD && stored > 0) {
            double heal = Math.min(stored, 1.0);
            player.setHealth(Math.min(player.getHealth() + heal, maxHp));
            plugin.getPlayerDataManager().setReservoirStored(player.getUniqueId(), stored - heal);
        }
    }

    @Override public String getDescription() { return "Stores HP when full; heals when critical."; }
    @Override public String getDescription(int level) {
        return "§7Stores up to §a" + (MAX_HP[level-1]/2) + "§7 hearts; releases below 30% HP."; }
}
