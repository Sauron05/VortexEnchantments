package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Phantasmal: Attacks bypass shields entirely. Additionally, the target's
 * Protection enchantment value is partially ignored (40/55/70%).
 */
public class PhantasmalEnchant extends VortexEnchant {

    public PhantasmalEnchant() {
        super("phantasmal", "Phantasmal", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        // Bypass shields if victim is blocking
        if (victim instanceof Player target && target.isBlocking()) {
            event.setDamage(event.getDamage()); // force full damage
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.REVERSE_PORTAL, 12, 0.4);
        }

        // Armor bypass: add back a portion of absorbed damage
        double bypassPct = cfgd("bypass_percent", 0.25 + level * 0.15);
        double absorbed = event.getDamage() - event.getFinalDamage();
        if (absorbed > 0) {
            event.setDamage(event.getDamage() + absorbed * bypassPct);
        }

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 6, 0.3);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_VEX_HURT, 0.5f, 1.8f);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.25 + level * 0.15) * 100);
        return "§7Bypasses §eshields §7+ ignores §c" + pct + "% §7of armor protection.";
    }
}
