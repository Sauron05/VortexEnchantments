package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Buried Treasure: Chance to find treasure items when digging. */
public class BuriedTreasureEnchant extends VortexEnchant {

    private static final Material[] TREASURES = {
            Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT,
            Material.IRON_INGOT, Material.LAPIS_LAZULI, Material.AMETHYST_SHARD,
            Material.GOLDEN_APPLE, Material.NAME_TAG, Material.SADDLE
    };

    public BuriedTreasureEnchant() { super("buried_treasure", "Buried Treasure", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double chance = cfg("chance", 2.0 + level * 1.5);
        if (!MathUtil.chance(chance)) return;
        Material treasure = TREASURES[(int) (Math.random() * TREASURES.length)];
        int amount = level >= 3 ? 2 : 1;
        Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        event.getBlock().getWorld().dropItemNaturally(loc, new ItemStack(treasure, amount));
        ParticleUtil.burst(loc, Particle.HAPPY_VILLAGER, 20, 1.0);
        SoundUtil.play(loc, Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.5f);
    }

    @Override public String getDescription() { return "Chance to discover treasure while digging."; }
    @Override public String getDescription(int level) {
        return "§7§e" + String.format("%.1f", 2.0 + level * 1.5) + "%§7 chance to unearth treasure items."; }
}
