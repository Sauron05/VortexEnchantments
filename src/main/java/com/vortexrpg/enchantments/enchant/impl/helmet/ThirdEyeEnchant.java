package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** ThirdEye: Reveal the nearest entity's health in the action bar on taking damage. */
public class ThirdEyeEnchant extends VortexEnchant {
    public ThirdEyeEnchant() { super("third_eye", "Third Eye", EnchantRarity.UNCOMMON, 1, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getDamager() instanceof LivingEntity attacker) {
            double pct = attacker.getHealth() / attacker.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue() * 100;
            player.sendActionBar(net.kyori.adventure.text.Component.text(
                "§c[Third Eye] §f" + attacker.getType().name() + " §7HP: §a" + (int)pct + "§7%"));
        }
    }

    @Override public String getDescription() { return "See attacker's health when they hit you."; }
    @Override public String getDescription(int level) { return "§7Reveals attacker HP in action bar."; }
}
