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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Blight: Attacks apply both Wither and Poison simultaneously.
 * Level 1: Wither I + Poison I for 3s. Level 2: Wither II + Poison I for 4s.
 * Level 3: Wither II + Poison II for 5s.
 */
public class BlightEnchant extends VortexEnchant {

    public BlightEnchant() {
        super("blight", "Blight", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int durationTicks = cfgi("duration_ticks", 40 + level * 20);
        int witherLevel = cfgi("wither_level", level >= 2 ? 1 : 0);
        int poisonLevel = cfgi("poison_level", level >= 3 ? 1 : 0);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, durationTicks, witherLevel, false, true));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, durationTicks, poisonLevel, false, true));

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.MYCELIUM, 15, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_HUSK_AMBIENT, 0.5f, 0.5f);
    }

    @Override
    public String getDescription(int level) {
        double secs = (40 + level * 20) / 20.0;
        return "§7Attacks apply §5Wither §7+ §2Poison §7for §e" + secs + "s§7.";
    }
}
