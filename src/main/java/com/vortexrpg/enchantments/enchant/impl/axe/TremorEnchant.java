package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Tremor: Hit target gets brief Nausea (1/1.5/2s) simulating screen shake.
 * Falls back to real Nausea I if ProtocolLib unavailable.
 */
public class TremorEnchant extends VortexEnchant {

    private static final int[] DURATION_TICKS = {20, 30, 40};

    public TremorEnchant() {
        super("tremor", "Tremor", EnchantRarity.RARE, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        int duration = cfgi("duration_ticks", DURATION_TICKS[level - 1]);
        // Apply brief Nausea I as fallback (ProtocolLib not required)
        victim.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 0, false, false, false));
    }

    @Override
    public String getDescription() { return "Hits cause Nausea to the target."; }

    @Override
    public String getDescription(int level) {
        return "§7Hit applies §dNausea§7 to target for §e" + (DURATION_TICKS[level-1]/20.0) + "s§7.";
    }
}
