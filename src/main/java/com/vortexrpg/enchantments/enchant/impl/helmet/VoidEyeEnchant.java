package com.vortexrpg.enchantments.enchant.impl.helmet;

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

import java.util.List;

/**
 * Void Eye: On hit, peek into the void — attacker is teleported 10 blocks into the air
 * and takes fall damage upon landing. 30s CD.
 */
public class VoidEyeEnchant extends VortexEnchant {
    public VoidEyeEnchant() { super("void_eye", "Void Eye", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!(attacker instanceof LivingEntity living)) return;
        if (isOnCooldown(victim)) return;

        double height = cfgd("launch_height", 8.0 + level * 4.0);
        living.setVelocity(new org.bukkit.util.Vector(0, height / 4.0, 0));

        ParticleUtil.spawn(living.getLocation(), Particle.REVERSE_PORTAL, 30, 1.0);
        SoundUtil.play(living.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        setCooldownFromConfig(victim, "cooldown", 30.0);
    }

    @Override public String getDescription(int level) {
        return "§7On hit: launch attacker §5" + (int)(8 + level * 4) + " §7blocks upward. §830s CD.";
    }
}
