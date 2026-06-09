package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** Last Stand Shield: Below 20% HP, blocking grants total immunity. */
public class LastStandShieldEnchant extends VortexEnchant {

    public LastStandShieldEnchant() { super("last_stand_shield", "Last Stand", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double threshold = cfg("hp-threshold", 0.2);
        if (player.getHealth() / maxHp > threshold) return;
        if (isOnCooldown(player)) return;
        event.setCancelled(true);
        int duration = cfgi("immunity-ticks", 20 + level * 10);
        setCooldownSeconds(player, duration / 20.0 + cfg("cooldown", 30.0 - level * 5));
    }

    @Override public String getDescription() { return "Below 20% HP: blocking = total immunity."; }
    @Override public String getDescription(int level) {
        return "§7Block below 20% HP: §6total immunity§7. CD: §e" + (int)(30 - level * 5) + "s§7."; }
}
