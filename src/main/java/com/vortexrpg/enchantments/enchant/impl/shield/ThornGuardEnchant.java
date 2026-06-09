package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Thorn Guard: Blocking reflects small damage to melee attackers. */
public class ThornGuardEnchant extends VortexEnchant {

    public ThornGuardEnchant() { super("thorn_guard", "Thorn Guard", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        double reflect = cfg("reflect", 0.5 + level * 0.5);
        attacker.damage(reflect);
    }

    @Override public String getDescription() { return "Blocking reflects damage to attackers."; }
    @Override public String getDescription(int level) {
        return "§7Block: reflect §c" + (0.5 + level * 0.5) + "♥§7 to melee attackers."; }
}
