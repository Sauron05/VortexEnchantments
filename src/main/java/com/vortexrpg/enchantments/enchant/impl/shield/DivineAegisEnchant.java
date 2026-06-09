package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Divine Aegis: Blocking heals all nearby allies for percentage of blocked damage. */
public class DivineAegisEnchant extends VortexEnchant {

    public DivineAegisEnchant() { super("divine_aegis", "Divine Aegis", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        double radius = cfg("radius", 6.0 + level * 2);
        double healPercent = cfg("heal-percent", 5.0 + level * 3);
        double heal = event.getDamage() * (healPercent / 100.0);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            if (e instanceof Player ally) {
                double maxHp = ally.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                ally.setHealth(Math.min(ally.getHealth() + heal, maxHp));
                ParticleUtil.spawn(ally.getLocation().add(0, 1, 0), Particle.HEART, 3, 0.5);
            }
        }
    }

    @Override public String getDescription() { return "Blocking heals nearby allies."; }
    @Override public String getDescription(int level) {
        return "§7Block: heal allies in §e" + (int)(6 + level * 2) + "b§7 for §a" + (int)(5 + level * 3) + "%§7 of damage."; }
}
