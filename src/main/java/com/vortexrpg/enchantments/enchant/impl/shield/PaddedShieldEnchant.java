package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;

/** Padded Shield: Less durability loss when blocking. */
public class PaddedShieldEnchant extends VortexEnchant {

    public PaddedShieldEnchant() { super("padded_shield", "Padded Shield", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        double saveChance = cfg("save-chance", 20.0 + level * 15);
        if (com.vortexrpg.enchantments.util.MathUtil.chance(saveChance)) {
            var shield = player.getInventory().getItemInOffHand();
            if (shield.getType() == org.bukkit.Material.SHIELD && shield.getItemMeta() instanceof Damageable dmg) {
                int restored = cfgi("restored", 1);
                dmg.setDamage(Math.max(0, dmg.getDamage() - restored));
                shield.setItemMeta(dmg);
            }
        }
    }

    @Override public String getDescription() { return "Less durability loss when blocking."; }
    @Override public String getDescription(int level) {
        return "§7Block: §a" + (int)(20 + level * 15) + "%§7 chance to save §e1§7 durability."; }
}
