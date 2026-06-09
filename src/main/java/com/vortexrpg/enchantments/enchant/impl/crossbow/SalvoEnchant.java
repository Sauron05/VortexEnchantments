package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.List;

/** Salvo: Store 2/3/4 loaded bolts; fire all rapidly before needing full reload. */
public class SalvoEnchant extends VortexEnchant {
    public SalvoEnchant() { super("salvo", "Salvo", EnchantRarity.EPIC, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        int maxBolts = cfgi("max_bolts", level + 1);
        int stored = plugin.getPlayerDataManager().getInt(shooter.getUniqueId(), "salvo_bolts");
        if (stored < maxBolts) {
            plugin.getPlayerDataManager().setInt(shooter.getUniqueId(), "salvo_bolts", stored + 1);
        }
    }

    @Override public String getDescription() { return "Load multiple bolts for a rapid burst fire."; }
    @Override public String getDescription(int level) { return "§7Load up to §e" + (level+1) + "§7 bolts for rapid burst fire."; }
}
