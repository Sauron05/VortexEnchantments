package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Rebuff: Blocking launches attacker back 2/3/4 blocks. */
public class RebuffEnchant extends VortexEnchant {
    private static final double[] POWER = {2.0, 3.0, 4.0};

    public RebuffEnchant() { super("rebuff", "Rebuff", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled() || !player.isBlocking()) return;
        if (!(event.getDamager() instanceof org.bukkit.entity.LivingEntity attacker)) return;
        double power = cfg("power", POWER[level-1]) / 10.0;
        Vector kb = attacker.getLocation().toVector().subtract(player.getLocation().toVector())
            .normalize().multiply(power).setY(0.3);
        attacker.setVelocity(kb);
    }

    @Override public String getDescription() { return "Blocking launches attackers away."; }
    @Override public String getDescription(int level) {
        return "§7Block: knock attacker §a" + POWER[level-1] + "§7 blocks away."; }
}
