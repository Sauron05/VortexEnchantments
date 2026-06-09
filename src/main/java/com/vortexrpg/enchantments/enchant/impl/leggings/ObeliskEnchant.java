package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Obelisk: Plant flag on sneak; enemies near flag take +10/15/20% extra damage from you. */
public class ObeliskEnchant extends VortexEnchant {
    private static final double[] BONUS = {0.10, 0.15, 0.20};
    private static final int RADIUS = 8;

    public ObeliskEnchant() { super("obelisk", "Obelisk", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onToggleSneak(org.bukkit.event.player.PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled() || !event.isSneaking()) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, 30);
        // Store flag location
        plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "obelisk_x", player.getLocation().getX());
        plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "obelisk_y", player.getLocation().getY());
        plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "obelisk_z", player.getLocation().getZ());
        player.sendActionBar(net.kyori.adventure.text.Component.text("§5[Obelisk] §7Flag planted!"));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, LivingEntity target, int level) {
        if (!isEnabled()) return;
        double x = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "obelisk_x");
        if (x == 0) return;
        double y = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "obelisk_y");
        double z = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "obelisk_z");
        org.bukkit.Location flag = new org.bukkit.Location(player.getWorld(), x, y, z);
        if (target.getLocation().distance(flag) <= RADIUS) {
            double bonus = cfg("bonus", BONUS[level-1]);
            event.setDamage(event.getDamage() * (1.0 + bonus));
        }
    }

    @Override public String getDescription() { return "Plant a flag; enemies near it take bonus damage."; }
    @Override public String getDescription(int level) {
        return "§7Sneak: plant flag. Enemies within §a" + RADIUS + "§7 blocks take §a+" + (int)(BONUS[level-1]*100) + "§a%§7 dmg."; }
}
