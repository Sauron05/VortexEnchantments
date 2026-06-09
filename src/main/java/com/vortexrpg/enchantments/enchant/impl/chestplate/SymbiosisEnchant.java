package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Symbiosis: Deal bonus damage equal to 5/8/12% of damage you just took. */
public class SymbiosisEnchant extends VortexEnchant {
    private static final double[] RATIO = {0.05, 0.08, 0.12};

    public SymbiosisEnchant() { super("symbiosis", "Symbiosis", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double stored = event.getFinalDamage();
        plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "symbiosis_stored", stored);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity target, int level) {
        if (!isEnabled()) return;
        double stored = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "symbiosis_stored");
        if (stored <= 0) return;
        double ratio = cfg("ratio", RATIO[level-1]);
        event.setDamage(event.getDamage() + stored * ratio);
        plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "symbiosis_stored", 0);
    }

    @Override public String getDescription() { return "Pain converts to power on next hit."; }
    @Override public String getDescription(int level) {
        return "§7" + (int)(RATIO[level-1]*100) + "§a%§7 of damage taken added to next attack."; }
}
