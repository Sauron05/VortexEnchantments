package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Plague Spreader: Right-click to inflict Wither + Poison on nearby hostiles. */
public class PlagueSpreaderEnchant extends VortexEnchant {

    public PlagueSpreaderEnchant() { super("plague_spreader", "Plague Spreader", EnchantRarity.EPIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;
        double radius = cfg("radius", 4.0 + level * 2);
        int duration = cfgi("duration", 40 + level * 20);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e instanceof Monster) {
                e.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration, level - 1));
                e.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, level - 1));
            }
        }
        setCooldownFromConfig(player, "cooldown", 15);
    }

    @Override public String getDescription() { return "Right-click: Wither + Poison nearby mobs."; }
    @Override public String getDescription(int level) {
        return "§7Right-click: §5Wither " + level + "§7 + §2Poison " + level + "§7 AoE."; }
}
