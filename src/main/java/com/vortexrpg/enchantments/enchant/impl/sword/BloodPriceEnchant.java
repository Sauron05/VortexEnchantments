package com.vortexrpg.enchantments.enchant.impl.sword;

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
 * Blood Price: Sacrifice 2/1.5/1 hearts of your own health to deal
 * 4/5/6 hearts of bonus damage. High risk, high reward.
 */
public class BloodPriceEnchant extends VortexEnchant {

    public BloodPriceEnchant() {
        super("blood_price", "Blood Price", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double selfDamage = cfgd("self_damage", 5.0 - level);
        double bonusDamage = cfgd("bonus_damage", 6.0 + level * 2.0);
        double minHealth = cfgd("min_health", 2.0);

        if (attacker.getHealth() <= minHealth + selfDamage) return;

        attacker.setHealth(attacker.getHealth() - selfDamage);
        event.setDamage(event.getDamage() + bonusDamage);

        ParticleUtil.spawn(attacker.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 5, 0.3);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 10, 0.4);
        SoundUtil.play(attacker.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.0f, 1.5f);

        attacker.sendMessage("§4[Blood Price] §7Sacrificed §c" + String.format("%.1f", selfDamage / 2)
            + " hearts §7for §c" + String.format("%.1f", bonusDamage / 2) + " hearts §7bonus damage!");
    }

    @Override
    public String getDescription(int level) {
        double self = (5.0 - level) / 2;
        double bonus = (6.0 + level * 2.0) / 2;
        return "§7Sacrifice §c" + String.format("%.1f", self) + "\u2764§7 to deal §c+"
            + String.format("%.1f", bonus) + "\u2764§7 bonus damage.";
    }
}
