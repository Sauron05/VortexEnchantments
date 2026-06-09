package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Ground Pound: Sneak+mine creates deep hole, damaging entities below. */
public class GroundPoundEnchant extends VortexEnchant {
    private static final double[] DAMAGE = {2, 3, 4};

    public GroundPoundEnchant() { super("ground_pound", "Ground Pound", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isSneaking()) return;
        int depth = cfgi("depth", 3);
        double damage = cfg("damage", DAMAGE[level - 1]);
        Block base = event.getBlock();
        for (int i = 1; i <= depth; i++) {
            Block below = base.getRelative(0, -i, 0);
            if (!below.getType().isAir() && below.getType() != org.bukkit.Material.BEDROCK) {
                below.breakNaturally(player.getInventory().getItemInMainHand());
            }
        }
        for (LivingEntity e : MathUtil.getNearbyLiving(base.getLocation().add(0, -2, 0), 2.0)) {
            if (e.equals(player)) continue;
            e.damage(damage, player);
        }
    }

    @Override public String getDescription() { return "Sneak-mining creates deep hole with damage."; }
    @Override public String getDescription(int level) {
        return "§7Sneak+dig: 3-deep hole + §c" + (int) DAMAGE[level - 1] + "♥§7 to entities below."; }
}
