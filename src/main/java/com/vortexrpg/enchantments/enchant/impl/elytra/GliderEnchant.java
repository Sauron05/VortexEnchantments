package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;

/** Glider: Chance to negate elytra durability loss each second while gliding. */
public class GliderEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.35, 0.50, 0.70};

    public GliderEnchant() { super("glider", "Glider", EnchantRarity.COMMON, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        if (Math.random() < cfgd("save_chance", CHANCE[level - 1])) {
            ItemStack chest = player.getInventory().getChestplate();
            if (chest != null && chest.getItemMeta() instanceof Damageable dmg && dmg.getDamage() > 0) {
                dmg.setDamage(dmg.getDamage() - 1);
                chest.setItemMeta(dmg);
            }
        }
    }

    @Override public String getDescription() { return "Reduces elytra durability loss while gliding."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level - 1] * 100) + "%§7 chance to negate durability loss per second."; }
}
