package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Laden: Higher carry weight (inventory fullness) → more damage reduction (up to 10/20/30%). */
public class LadenEnchant extends VortexEnchant {
    private static final double[] CAP = {0.10, 0.20, 0.30};

    public LadenEnchant() { super("laden", "Laden", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        long filledSlots = java.util.Arrays.stream(player.getInventory().getContents())
            .filter(i -> i != null && i.getType() != org.bukkit.Material.AIR).count();
        double fraction = filledSlots / 36.0;
        double cap = cfg("cap", CAP[level-1]);
        event.setDamage(event.getDamage() * (1.0 - fraction * cap));
    }

    @Override public String getDescription() { return "Fuller inventory = more DR."; }
    @Override public String getDescription(int level) {
        return "§7Full inventory: §a-" + (int)(CAP[level-1]*100) + "§a%§7 damage (scales with fullness)."; }
}
