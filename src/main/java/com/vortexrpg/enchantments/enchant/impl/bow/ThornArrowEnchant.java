package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Thorn Arrow: Arrow embeds in target; movement deals 0.5♥ per block moved. Lasts 6/8/10s.
 */
public class ThornArrowEnchant extends VortexEnchant {

    private static final int[] EMBED_SECS = {6, 8, 10};

    public ThornArrowEnchant() {
        super("thorn_arrow", "Thorn Arrow", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        int secs = cfgi("embed_duration_seconds", EMBED_SECS[level - 1]);
        double damagePerBlock = cfg("damage_per_block_moved", 1.0);

        Location[] lastLoc = {victim.getLocation().clone()};
        long endTime = System.currentTimeMillis() + secs * 1000L;

        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (!victim.isValid() || victim.isDead() || System.currentTimeMillis() > endTime) {
                task.cancel(); return;
            }
            double moved = victim.getLocation().distance(lastLoc[0]);
            if (moved > 0.3) {
                victim.damage(moved * damagePerBlock, shooter);
            }
            lastLoc[0] = victim.getLocation().clone();
        }, 2L, 2L);
    }

    @Override
    public String getDescription() { return "Embeds in target, dealing damage for every block they move."; }

    @Override
    public String getDescription(int level) {
        return "§7Embedded §e" + EMBED_SECS[level-1] + "s§7: §c0.5♥§7 per block target moves.";
    }
}
