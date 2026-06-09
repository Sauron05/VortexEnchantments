package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Foxfoot: Stepping on pressure plates, tripwires, or sculk sensors doesn't trigger them. */
public class FoxfootEnchant extends VortexEnchant {
    public FoxfootEnchant() { super("foxfoot", "Foxfoot", EnchantRarity.RARE, 1, List.of(ItemTarget.BOOTS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        // Handled via server-side block activation cancel in EnchantListener
    }

    @Override public String getDescription() { return "Silent step — doesn't trigger pressure plates."; }
    @Override public String getDescription(int level) { return "§7Steps silently; pressure plates, tripwires inactive."; }
}
