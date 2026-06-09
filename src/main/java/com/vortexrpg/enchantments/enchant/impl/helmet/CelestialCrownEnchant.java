package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Celestial Crown: Passively gains ALL positive potion effects at level I while above 80% HP.
 */
public class CelestialCrownEnchant extends VortexEnchant {
    private static final PotionEffectType[] BUFFS = {
        PotionEffectType.SPEED, PotionEffectType.STRENGTH, PotionEffectType.RESISTANCE,
        PotionEffectType.HASTE, PotionEffectType.REGENERATION, PotionEffectType.JUMP_BOOST
    };

    public CelestialCrownEnchant() { super("celestial_crown", "Celestial Crown", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double threshold = cfgd("hp_threshold", 0.80);
        if (player.getHealth() / maxHp < threshold) return;

        int amplifier = level - 1;
        for (PotionEffectType type : BUFFS) {
            if (!player.hasPotionEffect(type)) {
                player.addPotionEffect(new PotionEffect(type, 60, amplifier, true, false, false));
            }
        }
        ParticleUtil.spawn(player.getLocation().add(0, 2.5, 0), Particle.END_ROD, 2, 0.3);
    }

    @Override public String getDescription(int level) {
        return "§7Above 80% HP: gain §6ALL §7positive buffs at level §a" + level + "§7.";
    }
}
