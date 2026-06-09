package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Set;

/** Germinate: Right-click sapling to force 1 growth stage. 20/15/10s cooldown. */
public class GerminateEnchant extends VortexEnchant {
    private static final int[] COOLDOWN = {20, 15, 10};

    public GerminateEnchant() { super("germinate", "Germinate", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    private static final Set<Material> SAPLINGS = Set.of(
        Material.OAK_SAPLING, Material.BIRCH_SAPLING, Material.SPRUCE_SAPLING,
        Material.JUNGLE_SAPLING, Material.ACACIA_SAPLING, Material.DARK_OAK_SAPLING,
        Material.CHERRY_SAPLING, Material.MANGROVE_PROPAGULE
    );

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Block clicked = event.getClickedBlock();
        if (clicked == null) return;
        if (!SAPLINGS.contains(clicked.getType())) return;
        if (isOnCooldown(player)) return;
        int cd = cfgi("cooldown_seconds", COOLDOWN[level-1]);
        setCooldownSeconds(player, cd);
        if (clicked.getBlockData() instanceof Ageable ageable) {
            int next = Math.min(ageable.getAge() + 1, ageable.getMaximumAge());
            ageable.setAge(next);
            clicked.setBlockData(ageable);
        }
    }

    @Override public String getDescription() { return "Right-click saplings to advance growth."; }
    @Override public String getDescription(int level) {
        return "§7Force 1 growth stage on saplings. §a" + COOLDOWN[level-1] + "s§7 cooldown."; }
}
