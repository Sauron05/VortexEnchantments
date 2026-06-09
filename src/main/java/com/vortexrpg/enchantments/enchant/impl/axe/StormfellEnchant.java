package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Stormfell: Critical hits (falling) summon lightning on the target.
 * Level 1: 25% chance. Level 2: 40% chance. Level 3: 55% chance + extra dmg.
 */
public class StormfellEnchant extends VortexEnchant {

    public StormfellEnchant() {
        super("stormfell", "Stormfell", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        if (attacker.getFallDistance() < 0.5) return;

        double chance = cfgd("chance", 0.10 + level * 0.15);
        if (Math.random() > chance) return;

        victim.getWorld().strikeLightningEffect(victim.getLocation());

        double lightningDmg = cfgd("lightning_damage", 3.0 + level * 2.0);
        victim.damage(lightningDmg, attacker);

        SoundUtil.play(victim.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 1.0f);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.15) * 100);
        double dmg = 3.0 + level * 2.0;
        return "§7Critical hits have §e" + pct + "% §7chance to call lightning (§c" + dmg + " dmg§7).";
    }
}
