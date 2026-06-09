package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Transpose: Sneak + right-click air to swap positions with the nearest mob in 10 blocks. */
public class TransposeEnchant extends VortexEnchant {
    public TransposeEnchant() { super("transpose", "Transpose", EnchantRarity.EPIC, 1, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled() || !player.isSneaking()) return;
        if (event.getClickedBlock() != null) return;
        if (isOnCooldown(player)) return;
        int r = cfgi("radius", 10);
        var nearest = player.getWorld().getNearbyLivingEntities(player.getLocation(), r, r, r,
            e -> !(e instanceof Player)).stream()
            .min(java.util.Comparator.comparingDouble(e -> e.getLocation().distanceSquared(player.getLocation())));
        if (nearest.isEmpty()) return;
        setCooldownSeconds(player, 15);
        var mob = nearest.get();
        var temp = player.getLocation().clone();
        player.teleport(mob.getLocation());
        mob.teleport(temp);
        player.sendActionBar(net.kyori.adventure.text.Component.text("§5[Transpose] §7Swapped positions!"));
    }

    @Override public String getDescription() { return "Sneak + right-click to swap with nearest mob."; }
    @Override public String getDescription(int level) { return "§7Sneak + right-click: swap with nearest mob (§a15s§7 cd)."; }
}
