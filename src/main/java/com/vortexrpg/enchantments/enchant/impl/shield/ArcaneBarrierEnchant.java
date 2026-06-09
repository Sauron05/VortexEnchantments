package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Arcane Barrier: Blocking magic damage grants brief magic resistance. */
public class ArcaneBarrierEnchant extends VortexEnchant {

    public ArcaneBarrierEnchant() { super("arcane_barrier", "Arcane Barrier", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.MAGIC
                && event.getCause() != EntityDamageEvent.DamageCause.DRAGON_BREATH) return;
        double reduction = cfg("reduction", 0.25 + level * 0.1);
        event.setDamage(event.getDamage() * (1.0 - reduction));
        int duration = cfgi("resist-duration", 40 + level * 20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 0, true, false));
    }

    @Override public String getDescription() { return "Blocking magic grants resistance."; }
    @Override public String getDescription(int level) {
        return "§7Block magic: §a" + (int)((0.25 + level * 0.1) * 100) + "%§7 reduction + §eResistance§7."; }
}
