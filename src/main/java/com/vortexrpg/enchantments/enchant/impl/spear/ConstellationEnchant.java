package com.vortexrpg.enchantments.enchant.impl.spear;

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
 * Constellation: Only at night — hits mark the target with star particles.
 * At 5 marks, a lightning bolt strikes the target dealing 3x the hit damage.
 */
public class ConstellationEnchant extends VortexEnchant {

    private static final String MARKS_KEY = "constellation_";

    public ConstellationEnchant() {
        super("constellation", "Constellation", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        long time = attacker.getWorld().getTime();
        if (time < 13000 || time > 23000) return; // night only

        var pdm = plugin.getPlayerDataManager();
        String key = MARKS_KEY + victim.getUniqueId();
        int marks = pdm.getInt(attacker.getUniqueId(), key) + 1;

        int triggerMarks = cfgi("trigger_marks", 5);

        if (marks >= triggerMarks) {
            double multiplier = cfgd("multiplier", 1.0 + level);
            double boltDmg = event.getDamage() * multiplier;

            victim.getWorld().strikeLightningEffect(victim.getLocation());
            victim.damage(boltDmg, attacker);

            ParticleUtil.spawn(victim.getLocation().add(0, 2, 0), Particle.END_ROD, 30, 1.0);
            SoundUtil.play(victim.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.5f);

            pdm.setInt(attacker.getUniqueId(), key, 0);
        } else {
            pdm.setInt(attacker.getUniqueId(), key, marks);
            ParticleUtil.spawn(victim.getLocation().add(0, 2, 0), Particle.END_ROD, 5, 0.3);
            SoundUtil.play(victim.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.5f, 1.5f + marks * 0.2f);
        }
    }

    @Override
    public String getDescription(int level) {
        int mult = 1 + level;
        return "§7Night: §e5 marks §7→ §blightning strike §7(§c" + mult + "x §7damage).";
    }
}
