package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.List;

/** Prowl: While blocking + sneaking, mobs lose aggro faster. */
public class ProwlEnchant extends VortexEnchant {

    public ProwlEnchant() { super("prowl", "Prowl", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking() || !player.isSneaking()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 20 != 0) return;
        double radius = cfg("radius", 8.0 + level * 2);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e instanceof Mob mob && mob.getTarget() != null && mob.getTarget().equals(player)) {
                double chance = cfg("clear-chance", 15.0 + level * 10);
                if (MathUtil.chance(chance)) {
                    mob.setTarget(null);
                }
            }
        }
    }

    @Override public String getDescription() { return "Block+sneak: mobs lose aggro."; }
    @Override public String getDescription(int level) {
        return "§7Block+sneak: §a" + (int)(15 + level * 10) + "% §7chance mobs forget you."; }
}
