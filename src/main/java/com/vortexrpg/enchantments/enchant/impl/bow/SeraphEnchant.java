package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Seraph: Arrows that hit airborne targets deal 30/45/60% bonus damage.
 * Punishes anyone in the air — jumpers, elytra users, levitating foes.
 */
public class SeraphEnchant extends VortexEnchant {

    public SeraphEnchant() {
        super("seraph", "Seraph", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        if (victim.isOnGround()) return;

        double bonus = cfgd("airborne_bonus", 0.15 + level * 0.15);
        event.setDamage(event.getDamage() * (1.0 + bonus));

        ParticleUtil.spawn(victim.getLocation(), Particle.END_ROD, 12, 0.5);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.15 + level * 0.15) * 100);
        return "§7+" + pct + "% damage to §bairborne §7targets.";
    }
}
