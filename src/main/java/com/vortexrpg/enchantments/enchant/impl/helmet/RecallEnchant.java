package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Recall: Right-click air while sneaking to return to last death location. */
public class RecallEnchant extends VortexEnchant {
    public RecallEnchant() { super("recall", "Recall", EnchantRarity.RARE, 1, List.of(ItemTarget.HELMET)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isSneaking()) return;
        if (event.getClickedBlock() != null) return;
        if (isOnCooldown(player)) return;
        Location last = player.getLastDeathLocation();
        if (last == null) { player.sendMessage("§cNo death point recorded."); return; }
        setCooldownSeconds(player, cfgi("cooldown_seconds", 120));
        player.teleport(last);
        player.sendMessage("§aTeleported to last death location.");
    }

    @Override public String getDescription() { return "Sneak right-click to teleport to last death."; }
    @Override public String getDescription(int level) { return "§7Sneak + right-click: return to last death. §a120s §7cooldown."; }
}
