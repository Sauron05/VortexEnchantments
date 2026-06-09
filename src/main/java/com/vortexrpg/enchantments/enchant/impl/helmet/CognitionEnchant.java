package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Cognition: On taking damage, gain Resistance I for 2/3/4s. */
public class CognitionEnchant extends VortexEnchant {
    private static final int[] DURATION = {2, 3, 4};

    public CognitionEnchant() { super("cognition", "Cognition", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        int dur = cfgi("duration", DURATION[level-1]);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * dur, 0));
    }

    @Override public String getDescription() { return "Taking damage grants brief Resistance."; }
    @Override public String getDescription(int level) {
        return "§7Taking damage: §aResistance I §7for §a" + DURATION[level-1] + "s§7."; }
}
