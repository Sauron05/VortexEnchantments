package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Venomtip: Hits poison the target for 3/5/7 seconds.
 */
public class VenomtipEnchant extends VortexEnchant {

    public VenomtipEnchant() {
        super("venomtip", "Venomtip", EnchantRarity.COMMON, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int durationTicks = cfgi("duration_ticks", (1 + level * 2) * 20);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, durationTicks, 0, false, true));
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ITEM_SLIME, 8, 0.4);
    }

    @Override
    public String getDescription(int level) {
        int secs = 1 + level * 2;
        return "§7Hits apply §2Poison §7for §e" + secs + "s§7.";
    }
}
