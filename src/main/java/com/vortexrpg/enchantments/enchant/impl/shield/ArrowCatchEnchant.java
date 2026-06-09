package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.List;

/** Arrow Catch: Chance to catch arrows when blocking projectiles. */
public class ArrowCatchEnchant extends VortexEnchant {

    public ArrowCatchEnchant() { super("arrow_catch", "Arrow Catch", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        if (!(event.getDamager() instanceof AbstractArrow arrow)) return;
        double chance = cfg("chance", 15.0 + level * 10);
        if (!MathUtil.chance(chance)) return;
        event.setCancelled(true);
        arrow.remove();
        player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
    }

    @Override public String getDescription() { return "Chance to catch arrows when blocking."; }
    @Override public String getDescription(int level) {
        return "§7Block arrows: §a" + (int)(15 + level * 10) + "%§7 chance to catch."; }
}
