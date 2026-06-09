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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Rally: Killing an enemy buffs all nearby allies.
 * Radius: 10/12/15 blocks. Grants Strength I + Regeneration I for 3/4/5s.
 */
public class RallyEnchant extends VortexEnchant {

    public RallyEnchant() {
        super("rally", "Rally", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 8.0 + level * 2.0);
        int durationTicks = cfgi("duration_ticks", 40 + level * 20);

        int buffed = 0;
        for (Player nearby : killer.getLocation().getNearbyPlayers(radius)) {
            if (nearby.equals(killer)) continue;

            nearby.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, durationTicks, 0, false, true));
            nearby.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, durationTicks, 0, false, true));
            ParticleUtil.spawn(nearby.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 8, 0.3);
            nearby.sendMessage("§e[Rally] §7" + killer.getName() + "'s kill rallies you!");
            buffed++;
        }

        killer.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, durationTicks, 0, false, true));

        if (buffed > 0) {
            SoundUtil.play(killer.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 0.8f, 1.2f);
            killer.sendMessage("§e[Rally] §7Rallied §6" + buffed + " §7allies!");
        }
    }

    @Override
    public String getDescription(int level) {
        int r = (int) (8 + level * 2);
        return "§7Kills buff allies within §e" + r + " blocks §7with Strength I + Regen I.";
    }
}
