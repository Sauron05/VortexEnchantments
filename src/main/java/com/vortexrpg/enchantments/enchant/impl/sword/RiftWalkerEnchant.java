package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Rift Walker: Teleport behind the target on hit and deal bonus backstab damage.
 * Cooldown: 8/6/4 seconds per level.
 */
public class RiftWalkerEnchant extends VortexEnchant {

    public RiftWalkerEnchant() {
        super("rift_walker", "Rift Walker", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 10.0 - level * 2.0);
        double bonusDamage = cfgd("bonus_damage", 1.5 + level * 0.5);

        setCooldownSeconds(attacker, cooldown);

        Vector behindDir = victim.getLocation().getDirection().normalize().multiply(-1.5);
        Location behind = victim.getLocation().clone().add(behindDir);
        behind.setY(victim.getLocation().getY());
        behind.setYaw(victim.getLocation().getYaw());
        behind.setPitch(0);

        ParticleUtil.spawn(attacker.getLocation(), Particle.REVERSE_PORTAL, 20, 0.5);
        attacker.teleport(behind);
        ParticleUtil.spawn(behind, Particle.REVERSE_PORTAL, 20, 0.5);
        SoundUtil.play(behind, Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1.5f);

        event.setDamage(event.getDamage() + bonusDamage);
        attacker.sendMessage("§5[Rift Walker] §7Backstab! §c+" + String.format("%.1f", bonusDamage) + " damage");
    }

    @Override
    public String getDescription(int level) {
        return "§7Teleport §5behind§7 the target. §c+" + String.format("%.1f", 1.5 + level * 0.5) + " §7bonus damage.";
    }
}
