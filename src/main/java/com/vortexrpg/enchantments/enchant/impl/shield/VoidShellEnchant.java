package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** Void Shell: Nullifies ender pearl and chorus fruit teleport damage while blocking. */
public class VoidShellEnchant extends VortexEnchant {

    public VoidShellEnchant() { super("void_shell", "Void Shell", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            // Block ender pearl landing damage
            String key = "void_shell_ep";
            long last = plugin.getPlayerDataManager().getLong(player.getUniqueId(), key, 0L);
            if (System.currentTimeMillis() - last < 2000) {
                double reduction = cfg("reduction", 0.5 + level * 0.15);
                event.setDamage(event.getDamage() * (1.0 - reduction));
            }
        }
    }

    @Override
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        // Track ender pearl usage
        if (event.getAction().isRightClick() && player.getInventory().getItemInMainHand().getType() == org.bukkit.Material.ENDER_PEARL) {
            plugin.getPlayerDataManager().setLong(player.getUniqueId(), "void_shell_ep", System.currentTimeMillis());
        }
    }

    @Override public String getDescription() { return "Nullifies teleport damage while blocking."; }
    @Override public String getDescription(int level) {
        return "§7Block: §d" + (int)((0.5 + level * 0.15) * 100) + "%§7 less ender pearl/chorus damage."; }
}
