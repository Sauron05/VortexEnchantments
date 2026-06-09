package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * CommanderBoots: While above 70% HP, nearby allies gain Speed.
 */
public class CommanderBootsEnchant extends VortexEnchant {
    public CommanderBootsEnchant() { super("commander_boots", "Commander Boots", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double maxHp = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        if (player.getHealth() / maxHp < cfgd("hp_threshold", 0.70)) return;
        double radius = cfgd("radius", 8.0);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            if (!(e instanceof Player ally)) continue;
            ally.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.SPEED, 30, level - 1, true, false, true));
        }
        ParticleUtil.spawn(player.getLocation().add(0, 2, 0), Particle.COMPOSTER, 2, 0.4);
    }

    @Override public String getDescription(int level) {
        return "§7Above 70% HP: allies within 8 blocks get §bSpeed " + level + "§7.";
    }
}
