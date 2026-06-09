package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Shock Mine: Mining sends shockwave damaging nearby mobs. */
public class ShockMineEnchant extends VortexEnchant {
    private static final double[] DAMAGE = {2, 3, 4};

    public ShockMineEnchant() { super("shock_mine", "Shock Mine", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!MathUtil.chance(cfg("chance", 15))) return;
        double damage = cfg("damage", DAMAGE[level - 1]);
        double radius = cfg("radius", 4.0);
        for (LivingEntity e : MathUtil.getNearbyLiving(event.getBlock().getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.damage(damage, player);
        }
        SoundUtil.play(event.getBlock().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.6f, 1.4f);
    }

    @Override public String getDescription() { return "Mining may damage nearby mobs."; }
    @Override public String getDescription(int level) {
        return "§7Mining: §c" + (int) DAMAGE[level - 1] + "♥§7 shockwave to nearby mobs."; }
}
