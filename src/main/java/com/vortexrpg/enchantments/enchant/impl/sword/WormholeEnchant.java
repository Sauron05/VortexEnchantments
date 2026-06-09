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

import java.util.List;

/**
 * Wormhole: Swap positions with the target on hit.
 * Cooldown: 12/10/8 seconds per level.
 */
public class WormholeEnchant extends VortexEnchant {

    public WormholeEnchant() {
        super("wormhole", "Wormhole", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 14.0 - level * 2.0);
        setCooldownSeconds(attacker, cooldown);

        Location attackerLoc = attacker.getLocation().clone();
        Location victimLoc = victim.getLocation().clone();

        ParticleUtil.spawn(attackerLoc, Particle.PORTAL, 30, 0.5);
        ParticleUtil.spawn(victimLoc, Particle.PORTAL, 30, 0.5);
        SoundUtil.play(attackerLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);

        attacker.teleport(victimLoc);
        victim.teleport(attackerLoc);

        ParticleUtil.spawn(attacker.getLocation(), Particle.REVERSE_PORTAL, 20, 0.5);
        ParticleUtil.spawn(victim.getLocation(), Particle.REVERSE_PORTAL, 20, 0.5);

        attacker.sendMessage("§5[Wormhole] §7Swapped positions!");
        if (victim instanceof Player p) {
            p.sendMessage("§5[Wormhole] §7You were swapped!");
        }
    }

    @Override
    public String getDescription(int level) {
        int cd = 14 - level * 2;
        return "§7Swap positions§7 with the target on hit. §8(§e" + cd + "s cooldown§8)";
    }
}
