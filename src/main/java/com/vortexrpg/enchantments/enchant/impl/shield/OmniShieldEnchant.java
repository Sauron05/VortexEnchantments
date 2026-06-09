package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** Omni Shield: Auto-blocks from all directions and ignores shield-piercing. */
public class OmniShieldEnchant extends VortexEnchant {

    public OmniShieldEnchant() { super("omni_shield", "Omni Shield", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        // Works even if not actively blocking (holding shield in offhand)
        if (player.getInventory().getItemInOffHand().getType() != org.bukkit.Material.SHIELD) return;
        double reduction = cfg("passive-reduction", 0.1 + level * 0.05);
        event.setDamage(event.getDamage() * (1.0 - reduction));
        // Extra reduction when actively blocking
        if (player.isBlocking()) {
            double activeBonus = cfg("active-bonus", 0.1 + level * 0.05);
            event.setDamage(event.getDamage() * (1.0 - activeBonus));
        }
    }

    @Override public String getDescription() { return "Auto-blocks from all directions."; }
    @Override public String getDescription(int level) {
        return "§7Passive: §a" + (int)((0.1 + level * 0.05) * 100) + "%§7 reduction. Blocking: §6+" + (int)((0.1 + level * 0.05) * 100) + "%§7 more."; }
}
