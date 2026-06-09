package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;

/** SturdyLine: Chance to not consume rod durability on use. */
public class SturdyLineEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.20, 0.35, 0.50};

    public SturdyLineEnchant() { super("sturdy_line", "Sturdy Line", EnchantRarity.COMMON, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH || event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            if (Math.random() < cfgd("save_chance", CHANCE[level - 1])) {
                ItemStack rod = player.getInventory().getItemInMainHand();
                if (rod.getItemMeta() instanceof Damageable dmg && dmg.getDamage() > 0) {
                    dmg.setDamage(Math.max(0, dmg.getDamage() - 1));
                    rod.setItemMeta(dmg);
                }
            }
        }
    }

    @Override public String getDescription() { return "Saves rod durability on catch."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level - 1] * 100) + "%§7 chance to negate durability loss on catch."; }
}
