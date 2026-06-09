package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/** Coral: Melee hit places coral block at target's feet after 1s delay. */
public class CoralEnchant extends VortexEnchant {
    private static final Material[] CORALS = {
        Material.BRAIN_CORAL_BLOCK, Material.BUBBLE_CORAL_BLOCK, Material.FIRE_CORAL_BLOCK,
        Material.HORN_CORAL_BLOCK, Material.TUBE_CORAL_BLOCK
    };

    public CoralEnchant() { super("coral", "Coral", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.TRIDENT)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;
        setCooldownSeconds(attacker, cfgi("cooldown_seconds", 4));
        int delayTicks = cfgi("delay_ticks", 20);
        new BukkitRunnable() {
            @Override public void run() {
                if (!target.isValid()) return;
                Block block = target.getLocation().getBlock();
                if (block.getType() == Material.AIR) {
                    Material coral = CORALS[(int)(Math.random() * CORALS.length)];
                    block.setType(coral);
                    SoundUtil.play(block.getLocation(), Sound.BLOCK_CORAL_BLOCK_PLACE, 1f, 1f);
                }
            }
        }.runTaskLater(plugin, delayTicks);
    }

    @Override public String getDescription() { return "Places coral at target's feet on hit."; }
    @Override public String getDescription(int level) {
        return "§7Hit: coral block placed at target feet after §e1s§7."; }
}
