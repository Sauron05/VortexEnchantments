package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Gouge: Disables target's shield for 4/5/6 seconds by tagging them in PDM.
 * CombatListener checks this before shield blocks.
 */
public class GougeEnchant extends VortexEnchant {

    private static final double[] DISABLE_SECS = {4.0, 5.0, 6.0};

    public GougeEnchant() {
        super("gouge", "Gouge", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double secs = cfg("disable_duration_seconds", DISABLE_SECS[level - 1]);
        long expiry = System.currentTimeMillis() + (long)(secs * 1000);
        plugin.getPlayerDataManager().setLong(victim.getUniqueId(), "gouge_shield_disabled", expiry);
        if (victim instanceof Player p) {
            p.sendMessage("§c[Gouge] §7Your shield is disabled for §e" + secs + "s§7!");
        }
    }

    /** Utility: check if a player's shield is gouged. */
    public static boolean isShieldDisabled(Player player) {
        long expiry = VortexEnchantments.getInstance().getPlayerDataManager()
            .getLong(player.getUniqueId(), "gouge_shield_disabled");
        return System.currentTimeMillis() < expiry;
    }

    @Override
    public String getDescription() { return "Disables the target's shield for several seconds."; }

    @Override
    public String getDescription(int level) {
        return "§7Hit: disables target shield for §c" + DISABLE_SECS[level-1] + "s§7.";
    }
}
