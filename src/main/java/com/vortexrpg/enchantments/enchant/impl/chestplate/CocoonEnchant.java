package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Cocoon: When dropping below 20% HP, enter a brief Resistance II cocoon for 3/4/5s. 60s cooldown. */
public class CocoonEnchant extends VortexEnchant {
    private static final int[] DUR = {3, 4, 5};

    public CocoonEnchant() { super("cocoon", "Cocoon", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double postHp = player.getHealth() - event.getFinalDamage();
        if (postHp > maxHp * 0.20) return;
        if (isOnCooldown(player)) return;
        int dur = cfgi("duration", DUR[level-1]);
        setCooldownSeconds(player, 60);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * dur, 1));
        com.vortexrpg.enchantments.util.ParticleUtil.ring(player.getLocation(), org.bukkit.Particle.CLOUD, 16, 1.2f);
    }

    @Override public String getDescription() { return "Near-death triggers a protective cocoon."; }
    @Override public String getDescription(int level) {
        return "§7Below 20% HP: §aResistance II§7 for §a" + DUR[level-1] + "s§7 (60s cooldown)."; }
}
