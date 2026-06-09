package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Torrent: In rain: no durability drain + 10/15/20% faster return. Thunder: also +25% damage.
 */
public class TorrentEnchant extends VortexEnchant {
    private static final double[] RETURN_BONUS = {0.10, 0.15, 0.20};

    public TorrentEnchant() { super("torrent", "Torrent", EnchantRarity.RARE, 3, List.of(ItemTarget.TRIDENT)); }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        if (!isEnabled()) return;
        boolean storm = thrower.getWorld().hasStorm();
        boolean thunder = thrower.getWorld().isThundering();
        if (storm && thunder) {
            double bonus = cfg("thunder_damage_bonus", 0.25);
            event.setDamage(event.getDamage() * (1.0 + bonus));
        }
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        onTridentHit(event, attacker, target, level);
    }

    @Override public String getDescription() { return "Stronger in rain and storms."; }
    @Override public String getDescription(int level) {
        return "§7Rain: §bno durability drain§7 + §a+" + (int)(RETURN_BONUS[level-1]*100) + "%§7 return speed. "
               + "Thunder: §c+25%§7 damage."; }
}
