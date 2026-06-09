package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Vertigo: On taking damage, attacker gets Nausea for 2/3/4s. */
public class VertigoEnchant extends VortexEnchant {
    private static final int[] DUR = {2, 3, 4};

    public VertigoEnchant() { super("vertigo", "Vertigo", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getDamager() instanceof org.bukkit.entity.LivingEntity attacker) {
            int dur = cfgi("duration", DUR[level-1]);
            attacker.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * dur, 0));
        }
    }

    @Override public String getDescription() { return "Attackers suffer Nausea when they hit you."; }
    @Override public String getDescription(int level) {
        return "§7Hitting you inflicts §aNausea§7 for §a" + DUR[level-1] + "s§7."; }
}
