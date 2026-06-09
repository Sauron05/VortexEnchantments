package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * AnchorLine — Fishing Rod (Rare, Max 3)
 * The fishing line stays taut: when reeling in an entity, also prevents their knockback resistance
 * and locks their position briefly (0.5/1/1.5s immobilize via velocity=0).
 */
public class AnchorLineEnchant extends VortexEnchant {

    public AnchorLineEnchant() {
        super("anchor_line", "AnchorLine", "fishingrod");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        double[] dur = {0.5, 1.0, 1.5};
        return "Hooking an entity immobilizes them for §e" + dur[level - 1] + "s§7.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) return;
        org.bukkit.entity.Entity caught = event.getCaught();
        if (!(caught instanceof org.bukkit.entity.LivingEntity le)) return;

        double[] durations = {0.5, 1.0, 1.5};
        long ticks = (long)(cfgd("immobilize_duration", durations[level - 1]) * 20);
        le.setAI(false);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> le.setAI(true), ticks);
    }
}
