package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Tempest Shield: Blocking in rain causes lightning to strike attackers. */
public class TempestShieldEnchant extends VortexEnchant {

    public TempestShieldEnchant() { super("tempest_shield", "Tempest Shield", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        if (!player.getWorld().hasStorm()) return;
        if (isOnCooldown(player)) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        double chance = cfg("chance", 20.0 + level * 10);
        if (!MathUtil.chance(chance)) return;
        attacker.getWorld().strikeLightningEffect(attacker.getLocation());
        double damage = cfg("lightning-damage", 2.0 + level * 2);
        attacker.damage(damage);
        SoundUtil.play(attacker.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 1.2f);
        setCooldownFromConfig(player, "cooldown", 5);
    }

    @Override public String getDescription() { return "Blocking in rain summons lightning."; }
    @Override public String getDescription(int level) {
        return "§7Block in rain: §e" + (int)(20 + level * 10) + "%§7 chance §blightning§7 strikes attacker."; }
}
