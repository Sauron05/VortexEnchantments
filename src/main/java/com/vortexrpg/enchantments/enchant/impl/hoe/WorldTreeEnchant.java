package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** World Tree: Right-click sapling to instantly grow it into a full tree. */
public class WorldTreeEnchant extends VortexEnchant {

    public WorldTreeEnchant() { super("world_tree", "World Tree", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (!block.getType().name().endsWith("_SAPLING")) return;
        if (isOnCooldown(player)) return;
        // Apply bone meal repeatedly to grow
        for (int i = 0; i < 10 + level * 5; i++) {
            block.applyBoneMeal(BlockFace.UP);
        }
        SoundUtil.play(block.getLocation(), Sound.BLOCK_AZALEA_LEAVES_PLACE, 1.0f, 0.6f);
        ParticleUtil.burst(block.getLocation().add(0.5, 1, 0.5), Particle.HAPPY_VILLAGER, 30, 2.0);
        setCooldownFromConfig(player, "cooldown", 30.0 - level * 5);
    }

    @Override public String getDescription() { return "Right-click sapling for instant tree."; }
    @Override public String getDescription(int level) {
        return "§7Right-click sapling: §ainstant growth§7. CD: §e" + (int)(30 - level * 5) + "s§7."; }
}
