package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Sprout: Right-click a crop to advance it by 1 growth stage. */
public class SproutEnchant extends VortexEnchant {

    public SproutEnchant() { super("sprout", "Sprout", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (!(block.getBlockData() instanceof Ageable age)) return;
        if (age.getAge() >= age.getMaximumAge()) return;
        if (isOnCooldown(player)) return;
        age.setAge(Math.min(age.getMaximumAge(), age.getAge() + level));
        block.setBlockData(age);
        ParticleUtil.burst(block.getLocation().add(0.5, 0.5, 0.5), Particle.HAPPY_VILLAGER, 10, 0.5);
        setCooldownFromConfig(player, "cooldown", 15.0 - level * 3);
    }

    @Override public String getDescription() { return "Right-click crop to grow it."; }
    @Override public String getDescription(int level) {
        return "§7Right-click: advance crop §a" + level + "§7 stage(s). CD: §e" + (int)(15 - level * 3) + "s§7."; }
}
