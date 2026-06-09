package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Amnesia: Hitting a Player scrambles their hotbar for 3/4/5 seconds.
 */
public class AmnesiaEnchant extends VortexEnchant {
    private static final int[] DURATION = {3, 4, 5};

    public AmnesiaEnchant() { super("amnesia", "Amnesia", EnchantRarity.LEGENDARY, 1, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (!(target instanceof Player victim)) return;
        if (isOnCooldown(shooter)) return;
        setCooldownSeconds(shooter, cfgi("cooldown", 12));

        PlayerInventory inv = victim.getInventory();
        ItemStack[] original = new ItemStack[9];
        for (int i = 0; i < 9; i++) original[i] = inv.getItem(i);

        // Shuffle hotbar
        List<ItemStack> items = new ArrayList<>(Arrays.asList(original));
        Collections.shuffle(items);
        for (int i = 0; i < 9; i++) inv.setItem(i, items.get(i));
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1.8f);

        int duration = cfgi("duration_" + level, DURATION[level-1]);
        new BukkitRunnable() {
            @Override public void run() {
                if (!victim.isOnline()) return;
                for (int i = 0; i < 9; i++) inv.setItem(i, original[i]);
            }
        }.runTaskLater(plugin, duration * 20L);
    }

    @Override public String getDescription() { return "Scrambles a hit player's hotbar temporarily."; }
    @Override public String getDescription(int level) {
        return "§7Hit player's hotbar shuffles for §e" + DURATION[level-1] + "s§7."; }
}
