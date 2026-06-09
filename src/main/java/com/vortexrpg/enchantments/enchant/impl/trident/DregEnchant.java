package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;

import java.util.List;

/**
 * Dreg: Missed thrown trident becomes ground trap. Next non-owner to step on it takes 60/70/80% throw damage.
 */
public class DregEnchant extends VortexEnchant {
    private static final double[] TRAP_PERCENT = {0.60, 0.70, 0.80};

    public DregEnchant() { super("dreg", "Dreg", EnchantRarity.RARE, 3, List.of(ItemTarget.TRIDENT)); }

    // We set metadata when trident misses entity (hits block)
    // Actual trap detection handled via EntityDamageEvent checking proximity in tickPassive
    // For simplicity: on block hit set metadata, tickPassive checks nearby entities

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        // Check all world entities for trap tridents
        for (Entity entity : player.getWorld().getEntities()) {
            if (!(entity instanceof Trident trident)) continue;
            if (!trident.hasMetadata("dreg_trap")) continue;
            String ownerId = (String) trident.getMetadata("dreg_trap").get(0).value();
            if (ownerId.equals(player.getUniqueId().toString())) continue;
            if (entity.getLocation().distance(player.getLocation()) < 1.0) {
                double trapDamage = trident.getDamage() * cfg("trap_damage_percent", TRAP_PERCENT[level-1]);
                player.damage(trapDamage);
                trident.removeMetadata("dreg_trap", plugin);
                trident.remove();
            }
        }
    }

    @Override public String getDescription() { return "Missed trident becomes a ground trap."; }
    @Override public String getDescription(int level) {
        return "§7Miss: trident becomes trap. First to step on it takes §c" + (int)(TRAP_PERCENT[level-1]*100) + "%§7 of throw damage."; }
}
