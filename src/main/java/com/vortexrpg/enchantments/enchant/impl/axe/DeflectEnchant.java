package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Deflect: Chance to deflect incoming projectiles back at the shooter while holding an axe.
 * Chance: 15/25/35%.
 */
public class DeflectEnchant extends VortexEnchant {

    public DeflectEnchant() {
        super("deflect", "Deflect", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) return;
        if (!(attacker instanceof LivingEntity livingAttacker)) return;

        double chance = cfgd("chance", 0.05 + level * 0.1);
        if (Math.random() > chance) return;

        event.setCancelled(true);

        Vector direction = livingAttacker.getLocation().toVector()
                .subtract(victim.getLocation().toVector()).normalize().multiply(2.0);

        victim.getWorld().spawnArrow(victim.getEyeLocation(), direction, 2.0f, 0.0f);

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 12, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.5f);
        victim.sendMessage("§6[Deflect] §7Projectile deflected!");
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.1) * 100);
        return "§7" + pct + "% chance to §6deflect §7projectiles back at attackers.";
    }
}
