package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

/** Lava Forge: Mining near lava auto-smelts drops and gives Fire Resistance. */
public class LavaForgeEnchant extends VortexEnchant {
    private static final Map<Material, Material> SMELT = Map.of(
            Material.RAW_IRON, Material.IRON_INGOT,
            Material.RAW_GOLD, Material.GOLD_INGOT,
            Material.RAW_COPPER, Material.COPPER_INGOT);

    public LavaForgeEnchant() { super("lava_forge", "Lava Forge", EnchantRarity.EPIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!hasLavaNearby(event.getBlock())) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60 + level * 20, 0, true, false));
        if (!event.getBlock().getType().name().endsWith("_ORE")) return;
        event.setDropItems(false);
        for (ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
            Material smelted = SMELT.get(drop.getType());
            if (smelted != null) drop = new ItemStack(smelted, drop.getAmount());
            event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation().add(0.5, 0.5, 0.5), drop);
        }
    }

    private boolean hasLavaNearby(Block block) {
        for (int x = -2; x <= 2; x++)
            for (int y = -2; y <= 2; y++)
                for (int z = -2; z <= 2; z++)
                    if (block.getRelative(x, y, z).getType() == Material.LAVA) return true;
        return false;
    }

    @Override public String getDescription() { return "Near lava: auto-smelt + fire resistance."; }
    @Override public String getDescription(int level) {
        return "§7Near lava: §6auto-smelt§7 + §cFire Resist§7 " + (3 + level) + "s."; }
}
