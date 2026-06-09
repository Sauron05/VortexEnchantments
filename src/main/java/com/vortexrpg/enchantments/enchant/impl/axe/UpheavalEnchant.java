package com.vortexrpg.enchantments.enchant.impl.axe;

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
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Upheaval: Launch target high into the air on hit, then deal bonus
 * fall-damage-amplified damage when they land. 
 */
public class UpheavalEnchant extends VortexEnchant {

    public UpheavalEnchant() {
        super("upheaval", "Upheaval", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 8.0);
        double launchY = cfgd("launch_power", 0.8 + level * 0.3);

        setCooldownSeconds(attacker, cooldown);

        Vector launch = new Vector(0, launchY, 0);
        victim.setVelocity(launch);

        ParticleUtil.spawn(victim.getLocation(), Particle.CLOUD, 15, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 0.6f);

        attacker.sendMessage("§6[Upheaval] §7Target launched skyward!");
        if (victim instanceof Player p) {
            p.sendMessage("§6[Upheaval] §7You were launched into the air!");
        }
    }

    @Override
    public String getDescription(int level) {
        return "§7Launch target §ehigh into the air§7. Gravity does the rest.";
    }
}
