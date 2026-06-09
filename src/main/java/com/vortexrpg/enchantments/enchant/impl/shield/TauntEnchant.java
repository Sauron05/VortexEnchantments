package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Taunt: On block, pull the attacker 2 blocks closer + apply Weakness I for 3s. */
public class TauntEnchant extends VortexEnchant {
    public TauntEnchant() { super("taunt", "Taunt", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled() || !player.isBlocking()) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        org.bukkit.util.Vector pull = player.getLocation().toVector().subtract(attacker.getLocation().toVector())
            .normalize().multiply(1.5 * level);
        attacker.setVelocity(attacker.getVelocity().add(pull));
        attacker.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.WEAKNESS, 60, 0));
    }

    @Override public String getDescription() { return "Blocking taunts attackers, pulling them in."; }
    @Override public String getDescription(int level) {
        return "§7Block: pull attacker §a" + (1.5*level) + "§7 blocks + §aWeakness I§7 for 3s."; }
}
