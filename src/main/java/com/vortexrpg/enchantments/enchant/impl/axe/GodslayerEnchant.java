package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Godslayer: Bonus damage that scales with target's max HP.
 * Deals +3/5/7% of target's max health as bonus damage.
 * Extra effective against bosses and tanky mobs.
 */
public class GodslayerEnchant extends VortexEnchant {

    public GodslayerEnchant() {
        super("godslayer", "Godslayer", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double maxHealth = victim.getAttribute(Attribute.MAX_HEALTH).getValue();
        double pctDamage = cfgd("hp_percent", 0.01 + level * 0.02);
        double bonus = maxHealth * pctDamage;
        double cap = cfgd("damage_cap", 15.0 + level * 5.0);
        bonus = Math.min(bonus, cap);

        event.setDamage(event.getDamage() + bonus);

        if (maxHealth >= 50) {
            ParticleUtil.spawn(victim.getLocation().add(0, 1.5, 0), Particle.ENCHANTED_HIT, 20, 0.6);
            SoundUtil.play(victim.getLocation(), Sound.ENTITY_WITHER_HURT, 0.5f, 1.2f);
        }
    }

    @Override
    public String getDescription(int level) {
        double pct = (0.01 + level * 0.02) * 100;
        return "§7Deals §c+" + pct + "% §7of target's max HP as bonus damage.";
    }
}
