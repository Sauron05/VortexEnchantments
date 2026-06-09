package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/** Ancient Forge: All ore drops auto-upgrade one tier. */
public class AncientForgeEnchant extends VortexEnchant {
    private static final Map<Material, Material> UPGRADE = Map.of(
            Material.RAW_COPPER, Material.RAW_IRON,
            Material.RAW_IRON, Material.RAW_GOLD,
            Material.RAW_GOLD, Material.DIAMOND,
            Material.COAL, Material.RAW_IRON,
            Material.LAPIS_LAZULI, Material.DIAMOND,
            Material.REDSTONE, Material.EMERALD);

    public AncientForgeEnchant() { super("ancient_forge", "Ancient Forge", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getBlock().getType().name().endsWith("_ORE")) return;
        event.setDropItems(false);
        for (ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
            Material upgraded = UPGRADE.get(drop.getType());
            if (upgraded != null) {
                drop = new ItemStack(upgraded, drop.getAmount());
            }
            event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation().add(0.5, 0.5, 0.5), drop);
        }
    }

    @Override public String getDescription() { return "Ore drops are upgraded one tier."; }
    @Override public String getDescription(int level) {
        return "§7Ore drops §6auto-upgrade§7 one tier (iron→gold, gold→diamond...)."; }
}
