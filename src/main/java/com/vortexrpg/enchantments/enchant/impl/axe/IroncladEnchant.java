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
 * Ironclad: Killing a mob grants absorption hearts.
 * Level 1: 2 hearts for 5s. Level 2: 3 hearts for 7s. Level 3: 4 hearts for 10s.
 */
public class IroncladEnchant extends VortexEnchant {

    public IroncladEnchant() {
        super("ironclad", "Ironclad", EnchantRarity.RARE, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int absorptionLevel = cfgi("absorption_level", level - 1);
        int durationTicks = cfgi("duration_ticks", 60 + level * 40);

        killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, durationTicks, absorptionLevel, false, true));

        ParticleUtil.spawn(killer.getLocation().add(0, 1, 0), Particle.HEART, 4, 0.5);
        SoundUtil.play(killer.getLocation(), Sound.ITEM_ARMOR_EQUIP_DIAMOND, 0.7f, 1.2f);
    }

    @Override
    public String getDescription(int level) {
        int hearts = level + 1;
        double secs = (60 + level * 40) / 20.0;
        return "§7Kills grant §e" + hearts + " absorption hearts §7for §e" + secs + "s§7.";
    }
}
