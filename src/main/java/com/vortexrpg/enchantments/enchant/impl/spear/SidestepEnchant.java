package com.vortexrpg.enchantments.enchant.impl.spear;

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
 * Sidestep: After landing a hit, gain a brief Speed II burst for 0.5/1/1.5s
 * to reposition or chase.
 */
public class SidestepEnchant extends VortexEnchant {

    public SidestepEnchant() {
        super("sidestep", "Sidestep", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int durationTicks = (int) (cfgd("duration", level * 0.5) * 20);
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Math.max(durationTicks, 10), 1, false, false));
    }

    @Override
    public String getDescription(int level) {
        String dur = String.format("%.1f", level * 0.5);
        return "§7After hit, gain §bSpeed II §7for §e" + dur + "s§7.";
    }
}
