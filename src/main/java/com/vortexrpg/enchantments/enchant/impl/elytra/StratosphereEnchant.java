package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Stratosphere: Resistance and Regeneration at extreme altitude while gliding. */
public class StratosphereEnchant extends VortexEnchant {

    public StratosphereEnchant() { super("stratosphere", "Stratosphere", EnchantRarity.EPIC, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        double minY = cfgd("min_y", 200.0);
        if (player.getLocation().getY() >= minY) {
            int resAmp = level >= 3 ? 1 : 0;
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, resAmp, true, false, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0, true, false, true));
        }
    }

    @Override public String getDescription() { return "High-altitude buffs while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Grants §aResistance " + (level >= 3 ? "II" : "I") + " + Regeneration I§7 above §eY=200§7."; }
}
