package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Recoil: On bolt hit, the shooter is knocked backward.
 * Mobility tool — use the recoil to escape after shooting.
 */
public class RecoilEnchant extends VortexEnchant {

    public RecoilEnchant() {
        super("recoil", "Recoil", EnchantRarity.COMMON, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double force = cfgd("force", 0.3 + level * 0.2);
        Vector back = shooter.getLocation().getDirection().normalize().multiply(-force).setY(0.3);
        shooter.setVelocity(back);
        shooter.setFallDistance(0);

        ParticleUtil.spawn(shooter.getLocation(), Particle.CLOUD, 4, 0.2);
    }

    @Override
    public String getDescription(int level) {
        return "§7Hit: §erecoil backward §7— escape after shooting.";
    }
}
