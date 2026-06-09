package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/** Alert: Warns of enemies behind while blocking via particles. */
public class AlertEnchant extends VortexEnchant {

    public AlertEnchant() { super("alert", "Alert", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 20 != 0) return;
        double radius = cfg("radius", 6.0 + level * 2);
        var dir = player.getLocation().getDirection();
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            var toEntity = e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            if (dir.angle(toEntity) > Math.PI / 2) {
                ParticleUtil.spawn(player.getLocation().add(0, 2.2, 0), Particle.ANGRY_VILLAGER, 3, 0.3);
                return;
            }
        }
    }

    @Override public String getDescription() { return "Warns of enemies behind while blocking."; }
    @Override public String getDescription(int level) {
        return "§7Block: §cwarning§7 particles when enemies behind in " + (int)(6 + level * 2) + "b."; }
}
