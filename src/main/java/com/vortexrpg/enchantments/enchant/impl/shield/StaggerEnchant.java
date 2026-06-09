package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Stagger: Blocked melee attacks slow the attacker briefly. */
public class StaggerEnchant extends VortexEnchant {

    public StaggerEnchant() { super("stagger", "Stagger", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        int duration = cfgi("slow-duration", 20 + level * 10);
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, level - 1));
    }

    @Override public String getDescription() { return "Blocked attacks slow the attacker."; }
    @Override public String getDescription(int level) {
        return "§7Block: attacker gets §9Slowness " + level + "§7 for §e" + ((20 + level * 10) / 20.0) + "s§7."; }
}
