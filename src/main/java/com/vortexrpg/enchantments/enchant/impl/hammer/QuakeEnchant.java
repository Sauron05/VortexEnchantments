package com.vortexrpg.enchantments.enchant.impl.hammer;

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
 * Quake: Hits create falling-block visual particles + 1/2/3 bonus damage.
 */
public class QuakeEnchant extends VortexEnchant {

    public QuakeEnchant() {
        super("quake", "Quake", EnchantRarity.COMMON, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double bonus = cfgd("bonus_damage", level);
        event.setDamage(event.getDamage() + bonus);

        ParticleUtil.drawCircle(victim.getLocation(), 2.0 + level, 12, Particle.DUST_PLUME);
        ParticleUtil.spawn(victim.getLocation(), Particle.CAMPFIRE_SIGNAL_SMOKE, 6, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3f, 1.5f);
    }

    @Override
    public String getDescription(int level) {
        return "§7Hits shake the ground: §c+" + level + " §7bonus damage.";
    }
}
