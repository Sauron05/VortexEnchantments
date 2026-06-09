package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Graft: Right-click a sapling to get a random different sapling type. */
public class GraftEnchant extends VortexEnchant {

    private static final Material[] SAPLINGS = {
            Material.OAK_SAPLING, Material.SPRUCE_SAPLING, Material.BIRCH_SAPLING,
            Material.JUNGLE_SAPLING, Material.ACACIA_SAPLING, Material.DARK_OAK_SAPLING,
            Material.CHERRY_SAPLING, Material.MANGROVE_PROPAGULE
    };

    public GraftEnchant() { super("graft", "Graft", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (event.getClickedBlock() == null) return;
        Material mat = event.getClickedBlock().getType();
        boolean isSapling = false;
        for (Material s : SAPLINGS) {
            if (s == mat) { isSapling = true; break; }
        }
        if (!isSapling) return;
        if (isOnCooldown(player)) return;
        Material newSapling;
        do {
            newSapling = SAPLINGS[(int) (Math.random() * SAPLINGS.length)];
        } while (newSapling == mat);
        event.getClickedBlock().getWorld().dropItemNaturally(
                event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(newSapling, 1));
        setCooldownFromConfig(player, "cooldown", 20.0 - level * 5);
    }

    @Override public String getDescription() { return "Right-click sapling for a different type."; }
    @Override public String getDescription(int level) {
        return "§7Right-click sapling: get §arandom different sapling§7. CD: §e" + (int)(20 - level * 5) + "s§7."; }
}
