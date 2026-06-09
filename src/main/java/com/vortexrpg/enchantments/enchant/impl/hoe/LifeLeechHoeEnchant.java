package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Life Leech Hoe: Harvesting mature crops heals you. */
public class LifeLeechHoeEnchant extends VortexEnchant {

    public LifeLeechHoeEnchant() { super("life_leech_hoe", "Life Leech Hoe", EnchantRarity.EPIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getBlock().getBlockData() instanceof Ageable age)) return;
        if (age.getAge() < age.getMaximumAge()) return;
        double heal = cfg("heal", 0.5 * level);
        double maxHealth = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(maxHealth, player.getHealth() + heal));
    }

    @Override public String getDescription() { return "Harvesting mature crops heals you."; }
    @Override public String getDescription(int level) {
        return "§7Harvest: heal §c+" + String.format("%.1f", 0.5 * level) + "♥§7."; }
}
