package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.List;

/** Feedback: Each shot increases reload speed by 8%/10%/12% stacking. Max 5 stacks. */
public class FeedbackEnchant extends VortexEnchant {
    private static final double[] BONUS = {0.08, 0.10, 0.12};
    public FeedbackEnchant() { super("feedback", "Feedback", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        int maxStacks = cfgi("max_stacks", 5);
        long decayMs = (long)(cfg("decay_seconds", 10.0) * 1000);
        int stacks = plugin.getPlayerDataManager().getInt(shooter.getUniqueId(), "feedback_stacks");
        if (stacks < maxStacks) {
            plugin.getPlayerDataManager().setInt(shooter.getUniqueId(), "feedback_stacks", stacks + 1);
            plugin.getPlayerDataManager().setLong(shooter.getUniqueId(), "feedback_last_shot", System.currentTimeMillis() + decayMs);
        }
    }

    @Override
    public void tickPassive(Player player, int level) {
        long decay = plugin.getPlayerDataManager().getLong(player.getUniqueId(), "feedback_last_shot");
        if (System.currentTimeMillis() > decay) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "feedback_stacks", 0);
        }
    }

    @Override public String getDescription() { return "Each shot boosts reload speed up to 5 stacks."; }
    @Override public String getDescription(int level) {
        return "§7Each shot: §a+" + (int)(BONUS[level-1]*100) + "%§7 reload speed (max §e5 stacks§7, decays after §e10s§7)."; }
}
