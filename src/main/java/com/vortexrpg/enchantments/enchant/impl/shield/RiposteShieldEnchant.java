package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Riposte — Shield (Epic, Max 3)
 * If you block a hit, instantly deal 40/60/80% of the blocked damage back as melee.
 */
public class RiposteShieldEnchant extends VortexEnchant {

    public RiposteShieldEnchant() {
        super("riposte_shield", "Riposte", "shield");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] ratios = {40, 60, 80};
        return "Blocking a hit deals §c" + ratios[level - 1] + "%§7 of the blocked damage back to the attacker.";
    }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!player.isBlocking()) return;
        Entity attacker = event.getDamager();
        if (!(attacker instanceof LivingEntity le)) return;

        double[] ratios = {0.40, 0.60, 0.80};
        double ratio = cfgd("ratio", ratios[level - 1]);
        double riposteDmg = event.getFinalDamage() * ratio;

        // Apply riposte after tick to avoid loop
        plugin.getServer().getScheduler().runTask(plugin, () -> le.damage(riposteDmg, player));
        player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 0.9f, 1.4f);
    }
}
