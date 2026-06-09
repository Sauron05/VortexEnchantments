package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Phantom Guard: Blocking gives brief invisibility after being hit. */
public class PhantomGuardEnchant extends VortexEnchant {

    public PhantomGuardEnchant() { super("phantom_guard", "Phantom Guard", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        if (isOnCooldown(player)) return;
        int duration = cfgi("invis-duration", 20 + level * 10);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0, true, false));
        setCooldownFromConfig(player, "cooldown", 15);
    }

    @Override public String getDescription() { return "Blocking gives brief invisibility."; }
    @Override public String getDescription(int level) {
        return "§7Block: §7brief §dInvisibility§7 for §e" + ((20 + level * 10) / 20.0) + "s§7."; }
}
