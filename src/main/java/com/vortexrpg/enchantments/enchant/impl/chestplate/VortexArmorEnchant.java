package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Vortex (chestplate): On taking projectile damage, launch projectile back at attacker at 25/35/50% power. */
public class VortexArmorEnchant extends VortexEnchant {
    private static final double[] RETURN = {0.25, 0.35, 0.50};

    public VortexArmorEnchant() { super("vortex_armor", "Vortex", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof org.bukkit.entity.Projectile proj)) return;
        if (!(proj.getShooter() instanceof org.bukkit.entity.LivingEntity shooter)) return;
        double power = cfg("return_power", RETURN[level-1]);
        shooter.damage(event.getDamage() * power, player);
    }

    @Override public String getDescription() { return "Projectiles that hit you partly return to sender."; }
    @Override public String getDescription(int level) {
        return "§7Projectile hits deal §a" + (int)(RETURN[level-1]*100) + "§a%§7 back to shooter."; }
}
