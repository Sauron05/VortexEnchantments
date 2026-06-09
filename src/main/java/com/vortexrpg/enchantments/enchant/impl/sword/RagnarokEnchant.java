package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Ragnarok: When below 20% health, enter Ragnarok mode for 5/7/10 seconds:
 * +100% damage, +50% speed, fire resistance. The last stand.
 * Long cooldown: 60/50/40 seconds.
 */
public class RagnarokEnchant extends VortexEnchant {

    public RagnarokEnchant() {
        super("ragnarok", "Ragnarok", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double maxHealth = attacker.getAttribute(Attribute.MAX_HEALTH).getValue();
        double threshold = cfgd("health_threshold", 0.20);

        if (attacker.getHealth() / maxHealth > threshold) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 70.0 - level * 10.0);
        int durationTicks = cfgi("duration_ticks", 100) + (level - 1) * 40 + (level == 3 ? 20 : 0);
        double damageBoost = cfgd("damage_boost", 2.0);

        setCooldownSeconds(attacker, cooldown);

        event.setDamage(event.getDamage() * damageBoost);

        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, durationTicks, 1, false, false));
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, durationTicks, 0, false, false));
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, durationTicks, 1, false, false));
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationTicks, 0, false, false));

        ParticleUtil.spawnHelix(attacker.getLocation(), Particle.FLAME, 5, 3.0);
        ParticleUtil.spawn(attacker.getLocation(), Particle.LAVA, 20, 1.0);
        SoundUtil.play(attacker.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.5f);

        attacker.sendMessage("§4§l[RAGNAROK] §c§lTHE END TIMES BEGIN! §7All buffs active!");

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (attacker.isOnline()) {
                attacker.sendMessage("§4[Ragnarok] §7The fury subsides...");
                ParticleUtil.spawn(attacker.getLocation(), Particle.SMOKE, 15, 0.5);
            }
        }, durationTicks);
    }

    @Override
    public String getDescription(int level) {
        int secs = 5 + (level - 1) * 2 + (level == 3 ? 1 : 0);
        return "§7Below 20% HP: enter §4Ragnarok§7 for §e" + secs + "s§7. §c+100% damage§7, speed, fire res.";
    }
}
