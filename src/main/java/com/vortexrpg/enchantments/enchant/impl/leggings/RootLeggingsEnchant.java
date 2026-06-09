package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Root (Leggings): Passive Slow falling + reduces fall damage by 25/40/60%. */
public class RootLeggingsEnchant extends VortexEnchant {
    private static final double[] FALL_REDUCE = {0.25, 0.40, 0.60};

    public RootLeggingsEnchant() { super("root_leggings", "Root", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        // Handled via onDamageTaken
    }

    @Override
    public void onDamageTaken(org.bukkit.event.entity.EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        // No-op here; fall damage handled separately in EnchantListener (EntityDamageEvent for FALL cause)
    }

    @Override public String getDescription() { return "Reduces fall damage."; }
    @Override public String getDescription(int level) {
        return "§7Reduces fall damage by §a" + (int)(FALL_REDUCE[level-1]*100) + "§a%§7."; }
}
