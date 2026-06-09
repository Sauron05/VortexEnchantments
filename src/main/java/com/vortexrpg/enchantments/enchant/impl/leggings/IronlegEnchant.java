package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Ironleg: Each of the 4 armor pieces reduces leg damage by 5/8/12%. Stacking is additive. */
public class IronlegEnchant extends VortexEnchant {
    private static final double[] REDUCE = {0.05, 0.08, 0.12};

    public IronlegEnchant() { super("ironleg", "Ironleg", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        // Count iron/diamond/netherite armor pieces player is wearing
        long metalPieces = java.util.Arrays.stream(player.getInventory().getArmorContents())
            .filter(i -> i != null && (i.getType().name().contains("IRON") || i.getType().name().contains("DIAMOND") || i.getType().name().contains("NETHERITE")))
            .count();
        double reduce = cfg("reduce", REDUCE[level-1]) * (metalPieces / 4.0);
        if (reduce > 0) event.setDamage(event.getDamage() * (1.0 - reduce));
    }

    @Override public String getDescription() { return "Metal armor pieces increase this DR."; }
    @Override public String getDescription(int level) {
        return "§7Up to §a-" + (int)(REDUCE[level-1]*100) + "§a%§7 DR (scales with metal armor worn)."; }
}
