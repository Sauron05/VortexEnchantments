package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Cache: On kill, store 1 item from the mob's drops; sneak + interact to retrieve. */
public class CacheEnchant extends VortexEnchant {
    public CacheEnchant() { super("cache", "Cache", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onKill(EntityDamageByEntityEvent event, Player player, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        int slots = cfgi("slots", level);
        int stored = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "cache_count");
        if (stored >= slots) return;
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "cache_count", stored + 1);
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "cache_stored_kill_type_" + stored,
            killed.getType().ordinal());
        player.sendActionBar(net.kyori.adventure.text.Component.text("§b[Cache] §7Stored kill #" + (stored+1)));
    }

    @Override public String getDescription() { return "Store kills; sneak-interact to recall."; }
    @Override public String getDescription(int level) {
        return "§7Cache up to §a" + level + "§7 kill records on this helmet."; }
}
