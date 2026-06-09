package com.vortexrpg.enchantments.enchant.impl.leggings;

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
 * CommanderLegs: While at high HP, nearby allies gain passive damage resistance.
 */
public class CommanderLegsEnchant extends VortexEnchant {
    public CommanderLegsEnchant() { super("commander_legs", "Commander Legs", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double maxHp = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        if (player.getHealth() / maxHp < cfgd("hp_threshold", 0.70)) return;
        double radius = cfgd("radius", 8.0);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            if (!(e instanceof Player ally)) continue;
            org.bukkit.potion.PotionEffect eff = new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.RESISTANCE, 30, level - 1, true, false, true);
            ally.addPotionEffect(eff);
        }
        ParticleUtil.spawn(player.getLocation().add(0, 2, 0), Particle.HAPPY_VILLAGER, 2, 0.4);
    }

    @Override public String getDescription(int level) {
        return "§7Above 70% HP: allies within 8 blocks gain §bResistance " + level + "§7.";
    }
}
