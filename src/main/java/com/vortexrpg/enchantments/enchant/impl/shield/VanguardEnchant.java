package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Vanguard: First block after raising shield deals stronger reduction. */
public class VanguardEnchant extends VortexEnchant {

    public VanguardEnchant() { super("vanguard", "Vanguard", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        String key = "vanguard_last_block";
        long last = plugin.getPlayerDataManager().getLong(player.getUniqueId(), key, 0L);
        long now = System.currentTimeMillis();
        double window = cfg("window", 2000);
        if (now - last > window) {
            double bonus = cfg("first-block-reduction", 0.15 + level * 0.1);
            event.setDamage(event.getDamage() * (1.0 - bonus));
        }
        plugin.getPlayerDataManager().setLong(player.getUniqueId(), key, now);
    }

    @Override public String getDescription() { return "First block is stronger."; }
    @Override public String getDescription(int level) {
        return "§7First block: §a" + (int)((0.15 + level * 0.1) * 100) + "%§7 extra reduction."; }
}
