package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Static: On take damage, emit lightning particle and apply Slowness to attacker. */
public class StaticEnchant extends VortexEnchant {
    private static final int[] DUR = {1, 2, 3};

    public StaticEnchant() { super("static", "Static", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof org.bukkit.entity.LivingEntity attacker)) return;
        int dur = cfgi("duration", DUR[level-1]);
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * dur, 0));
        player.getWorld().strikeLightningEffect(player.getLocation());
    }

    @Override public String getDescription() { return "Discharge lightning to slow attackers."; }
    @Override public String getDescription(int level) {
        return "§7On hit: §aSlowness I§7 on attacker for §a" + DUR[level-1] + "s§7 + lightning effect."; }
}
