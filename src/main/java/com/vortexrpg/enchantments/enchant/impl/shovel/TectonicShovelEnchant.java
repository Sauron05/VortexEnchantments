package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Tectonic Shovel: Mining pushes entities away with damage. */
public class TectonicShovelEnchant extends VortexEnchant {
    private static final double[] DAMAGE = {2, 3, 4};

    public TectonicShovelEnchant() { super("tectonic_shovel", "Tectonic", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double radius = cfg("radius", 4.0);
        double damage = cfg("damage", DAMAGE[level - 1]);
        for (LivingEntity e : MathUtil.getNearbyLiving(event.getBlock().getLocation(), radius)) {
            if (e.equals(player)) continue;
            Vector push = e.getLocation().toVector().subtract(event.getBlock().getLocation().toVector()).normalize().multiply(1.2);
            push.setY(0.5);
            e.setVelocity(push);
            e.damage(damage, player);
        }
        SoundUtil.play(event.getBlock().getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 0.8f, 0.6f);
    }

    @Override public String getDescription() { return "Mining pushes and damages nearby enemies."; }
    @Override public String getDescription(int level) {
        return "§7Dig: push + §c" + (int) DAMAGE[level - 1] + "♥§7 to nearby mobs."; }
}
