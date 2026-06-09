package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Briar: On take damage, attacker gets Poison I for 2/3/4s. */
public class BriarEnchant extends VortexEnchant {
    private static final int[] DUR = {2, 3, 4};

    public BriarEnchant() { super("briar", "Briar", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        int dur = cfgi("duration", DUR[level-1]);
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * dur, 0));
    }

    @Override public String getDescription() { return "Attackers are poisoned on contact."; }
    @Override public String getDescription(int level) {
        return "§7Melee attackers get §aPoison I§7 for §a" + DUR[level-1] + "s§7."; }
}
