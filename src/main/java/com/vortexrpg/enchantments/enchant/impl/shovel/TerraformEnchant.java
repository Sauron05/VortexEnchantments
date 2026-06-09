package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Terraform: Dig 4 blocks in square, right-click center to replace all 4 with offhand block. */
public class TerraformEnchant extends VortexEnchant {
    public TerraformEnchant() { super("terraform", "Terraform", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getClickedBlock() == null) return;
        if (isOnCooldown(player)) return;
        org.bukkit.inventory.ItemStack offhand = player.getInventory().getItemInOffHand();
        if (offhand.getType().isAir() || !offhand.getType().isBlock()) return;
        int size = cfgi("pattern_size_" + level, 2 + level);
        setCooldownSeconds(player, cfgi("cooldown", 3));
        org.bukkit.block.Block center = event.getClickedBlock();
        int used = 0;
        for (int x = -size/2; x <= size/2; x++) {
            for (int z = -size/2; z <= size/2; z++) {
                if (used >= offhand.getAmount()) break;
                org.bukkit.block.Block b = center.getRelative(x, 0, z);
                if (b.getType().isSolid()) {
                    b.setType(offhand.getType());
                    used++;
                }
            }
        }
        offhand.setAmount(offhand.getAmount() - used);
        player.getInventory().setItemInOffHand(offhand.getAmount() <= 0 ? new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR) : offhand);
    }

    @Override public String getDescription() { return "Right-click to replace blocks in pattern with offhand block."; }
    @Override public String getDescription(int level) {
        return "§7Right-click: replace §e" + (2+level) + "×" + (2+level) + "§7 area with §boffhand block§7."; }
}
