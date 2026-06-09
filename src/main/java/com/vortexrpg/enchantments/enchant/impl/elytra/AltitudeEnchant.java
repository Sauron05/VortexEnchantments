package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Altitude: Resistance while gliding above Y=150. */
public class AltitudeEnchant extends VortexEnchant {

    public AltitudeEnchant() { super("altitude", "Altitude", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        double minY = cfgd("min_y", 150.0);
        if (player.getLocation().getY() >= minY) {
            int amp = level >= 3 ? 1 : 0;
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, amp, true, false, true));
        }
    }

    @Override public String getDescription() { return "Damage resistance at high altitude."; }
    @Override public String getDescription(int level) {
        return "§7Grants §aResistance " + (level >= 3 ? "II" : "I") + "§7 while gliding above §eY=150§7."; }
}
