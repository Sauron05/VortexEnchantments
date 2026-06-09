package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.List;

/**
 * Cascade: Kill with arrow reloads 1/1/2 free arrows (next shots don't consume arrows).
 */
@SuppressWarnings("deprecation")
public class CascadeEnchant extends VortexEnchant {

    private static final int[] FREE_ARROWS = {1, 1, 2};

    public CascadeEnchant() {
        super("cascade", "Cascade", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        int arrows = cfgi("free_arrows_per_kill", FREE_ARROWS[level - 1]);
        int maxStored = cfgi("max_stored", 3);
        int current = plugin.getPlayerDataManager().getInt(killer.getUniqueId(), "cascade_free_arrows");
        plugin.getPlayerDataManager().setInt(killer.getUniqueId(), "cascade_free_arrows",
            Math.min(current + arrows, maxStored));
        killer.sendMessage("§6[Cascade] §e" + arrows + " free arrow(s)!");
    }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        int free = plugin.getPlayerDataManager().getInt(shooter.getUniqueId(), "cascade_free_arrows");
        if (free > 0) {
            event.setConsumeArrow(false);
            plugin.getPlayerDataManager().setInt(shooter.getUniqueId(), "cascade_free_arrows", free - 1);
        }
    }

    @Override
    public String getDescription() { return "Arrow kills grant free arrows for the next shot(s)."; }

    @Override
    public String getDescription(int level) {
        return "§7Arrow kill: §a" + FREE_ARROWS[level-1] + " free arrow§7 (next shot(s) don't consume).";
    }
}
