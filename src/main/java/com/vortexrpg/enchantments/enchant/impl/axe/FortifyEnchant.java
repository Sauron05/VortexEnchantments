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
 * Fortify: Killing an enemy grants temporary Resistance + Fire Resistance.
 * Duration: 4/6/8 seconds.
 */
public class FortifyEnchant extends VortexEnchant {

    public FortifyEnchant() {
        super("fortify", "Fortify", EnchantRarity.RARE, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int durationTicks = cfgi("duration_ticks", 60 + level * 40);

        killer.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationTicks, 0, false, true));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, durationTicks, 0, false, true));

        ParticleUtil.spawn(killer.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 15, 0.5);
        SoundUtil.play(killer.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 0.8f, 1.2f);
        killer.sendMessage("§b[Fortify] §7Defenses bolstered!");
    }

    @Override
    public String getDescription(int level) {
        double secs = (60 + level * 40) / 20.0;
        return "§7Kills grant §bResistance I §7+ §6Fire Resistance §7for §e" + secs + "s§7.";
    }
}
