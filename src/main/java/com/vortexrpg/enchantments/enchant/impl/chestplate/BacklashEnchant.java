package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Backlash: Reflect 10/15/20% of melee damage back to attacker. */
public class BacklashEnchant extends VortexEnchant {
    private static final double[] REFLECT = {0.10, 0.15, 0.20};

    public BacklashEnchant() { super("backlash", "Backlash", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof org.bukkit.entity.LivingEntity attacker)) return;
        double reflect = cfg("reflect", REFLECT[level-1]);
        attacker.damage(event.getDamage() * reflect, player);
    }

    @Override public String getDescription() { return "Reflects damage back to attackers."; }
    @Override public String getDescription(int level) {
        return "§7Reflect §a" + (int)(REFLECT[level-1]*100) + "§a%§7 of melee damage."; }
}
