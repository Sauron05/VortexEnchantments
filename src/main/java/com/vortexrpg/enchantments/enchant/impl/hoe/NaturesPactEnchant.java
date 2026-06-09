package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;

import java.util.List;

/** Nature's Pact: Animals near you breed faster when holding hoe. */
public class NaturesPactEnchant extends VortexEnchant {

    public NaturesPactEnchant() { super("natures_pact", "Nature's Pact", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 100 != 0) return;
        int radius = cfgi("radius", 6 + level * 2);
        for (var entity : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (entity instanceof Animals animal) {
                if (animal.getLoveModeTicks() > 0) {
                    animal.setLoveModeTicks(animal.getLoveModeTicks() + cfgi("love-boost", 20 * level));
                }
                if (animal.getAge() < 0) {
                    animal.setAge(animal.getAge() + cfgi("growth-boost", 200 * level));
                }
            }
        }
    }

    @Override public String getDescription() { return "Animals near you breed/grow faster."; }
    @Override public String getDescription(int level) {
        return "§7Animals within §e" + (6 + level * 2) + "§7b: faster breeding & growth."; }
}
