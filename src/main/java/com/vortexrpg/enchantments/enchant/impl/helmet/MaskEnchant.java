package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Mask: Reduces first hit per combat by 15/25/35% (first strike discount). */
public class MaskEnchant extends VortexEnchant {
    private static final double[] REDUCE = {0.15, 0.25, 0.35};

    public MaskEnchant() { super("mask", "Mask", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        long lastHit = plugin.getPlayerDataManager().getLong(player.getUniqueId(), "mask_last_hit");
        long now = System.currentTimeMillis();
        long combatWindow = 8000L;
        if (now - lastHit > combatWindow) {
            double reduce = cfg("reduce", REDUCE[level-1]);
            event.setDamage(event.getDamage() * (1.0 - reduce));
        }
        plugin.getPlayerDataManager().setLong(player.getUniqueId(), "mask_last_hit", now);
    }

    @Override public String getDescription() { return "Reduces damage from first hit per fight."; }
    @Override public String getDescription(int level) {
        return "§7First hit in combat: §a-" + (int)(REDUCE[level-1]*100) + "§a%§7 damage."; }
}
