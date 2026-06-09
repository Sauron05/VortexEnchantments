package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** PhantomLimb: When HP drops below 25%, ignore next hit entirely. 90s cooldown. */
public class PhantomLimbEnchant extends VortexEnchant {
    public PhantomLimbEnchant() { super("phantom_limb", "Phantom Limb", EnchantRarity.EPIC, 1, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        if (player.getHealth() > maxHp * 0.25) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, 90);
        event.setCancelled(true);
        com.vortexrpg.enchantments.util.ParticleUtil.burst(player.getLocation(), org.bukkit.Particle.SMOKE, 20, 0.5f);
    }

    @Override public String getDescription() { return "Below 25% HP, ignore one hit."; }
    @Override public String getDescription(int level) { return "§7Below §c25%§7 HP: next hit ignored (§a90s§7 cd)."; }
}
