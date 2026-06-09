package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * AbsorbLegs: Small lifesteal on melee attacks.
 */
public class AbsorbLegsEnchant extends VortexEnchant {
    public AbsorbLegsEnchant() { super("absorb_legs", "Absorb Legs", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double pct = cfgd("steal_pct", 0.03 * level);
        double steal = event.getDamage() * pct;
        double maxHp = attacker.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        attacker.setHealth(Math.min(maxHp, attacker.getHealth() + steal));
    }

    @Override public String getDescription(int level) {
        return "§7Steal §c" + (3 * level) + "% §7of melee damage as HP.";
    }
}
