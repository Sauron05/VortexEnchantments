package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Brace: Blocking reduces knockback taken. */
public class BraceEnchant extends VortexEnchant {

    public BraceEnchant() { super("brace", "Brace", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        double reduction = cfg("kb-reduction", 0.3 + level * 0.2);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            var vel = player.getVelocity();
            player.setVelocity(vel.multiply(1.0 - reduction));
        }, 1L);
    }

    @Override public String getDescription() { return "Blocking reduces knockback."; }
    @Override public String getDescription(int level) {
        return "§7Block: §a" + (int)((0.3 + level * 0.2) * 100) + "%§7 less knockback."; }
}
