package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Reactive: When hit, gain a short Speed boost.
 */
public class ReactiveEnchant extends VortexEnchant {
    public ReactiveEnchant() { super("reactive", "Reactive", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        int dur = cfgi("speed_duration", 30 + level * 10);
        int amp = cfgi("speed_amplifier", level - 1);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, dur, amp, true, false, true));
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3f, 1.8f);
    }

    @Override public String getDescription(int level) {
        return "§7When hit: gain §aSpeed " + level + " §7briefly.";
    }
}
