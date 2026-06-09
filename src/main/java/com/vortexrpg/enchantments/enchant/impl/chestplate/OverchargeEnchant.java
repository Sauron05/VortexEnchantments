package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Overcharge: After not being hit for 5s, next hit is reduced by X%.
 */
public class OverchargeEnchant extends VortexEnchant {
    private static final Map<UUID, Long> LAST_HIT = new HashMap<>();
    private static final Map<UUID, Boolean> CHARGED = new HashMap<>();

    public OverchargeEnchant() { super("overcharge", "Overcharge", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long delay = cfgi("charge_delay_ms", 5000);
        Long last = LAST_HIT.get(player.getUniqueId());
        if (last == null || System.currentTimeMillis() - last >= delay) {
            CHARGED.put(player.getUniqueId(), true);
        }
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        LAST_HIT.put(victim.getUniqueId(), System.currentTimeMillis());
        Boolean charged = CHARGED.remove(victim.getUniqueId());
        if (Boolean.TRUE.equals(charged)) {
            double pct = cfgd("reduction_pct", 0.15 * level);
            event.setDamage(event.getDamage() * (1.0 - pct));
        }
    }

    @Override public String getDescription(int level) {
        return "§7After 5s without damage: next hit reduced by §a" + (15 * level) + "%§7.";
    }
}
