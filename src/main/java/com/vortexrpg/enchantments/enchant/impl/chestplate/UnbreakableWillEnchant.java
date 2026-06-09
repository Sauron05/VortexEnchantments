package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Unbreakable Will: Shield from death once, revive at 50% HP. Long cooldown.
 */
public class UnbreakableWillEnchant extends VortexEnchant {
    public UnbreakableWillEnchant() { super("unbreakable_will", "Unbreakable Will", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(victim)) return;
        if (victim.getHealth() - event.getFinalDamage() > 0) return;

        event.setCancelled(true);
        double maxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double reviveHp = cfgd("revive_pct", 0.30 + level * 0.10) * maxHp;
        victim.setHealth(reviveHp);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 1, true, false, true));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1, true, false, true));

        ParticleUtil.burst(victim.getLocation(), Particle.TOTEM_OF_UNDYING, 30, 2.0);
        SoundUtil.play(victim.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);
        setCooldownFromConfig(victim, "cooldown", 120.0);
    }

    @Override public String getDescription(int level) {
        return "§7Survive lethal damage, revive at §a" + (int)(30 + level * 10) + "% §7HP. §8120s CD.";
    }
}
