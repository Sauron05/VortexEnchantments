package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Sleeper: Arrow does 0 on impact. After 3/4/5s: deals triple/3.5×/4× as true damage.
 */
public class SleeperEnchant extends VortexEnchant {

    private static final int[] DELAY_SECS = {3, 4, 5};
    private static final double[] MULT = {3.0, 3.5, 4.0};

    public SleeperEnchant() {
        super("sleeper", "Sleeper", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double storedDamage = event.getDamage() * cfg("damage_multiplier", MULT[level - 1]);
        event.setDamage(0);
        long delayTicks = cfgi("delay_seconds", DELAY_SECS[level - 1]) * 20L;

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!victim.isValid() || victim.isDead()) return;
            victim.setHealth(Math.max(0, victim.getHealth() - storedDamage));
            victim.getWorld().strikeLightningEffect(victim.getLocation());
        }, delayTicks);
    }

    @Override
    public String getDescription() { return "Arrow does no damage on impact; delivers a massive delayed strike."; }

    @Override
    public String getDescription(int level) {
        return "§7Arrow: §c0 damage§7 on hit. After §e" + DELAY_SECS[level-1] + "s§7: §c×" + MULT[level-1] + "§7 true damage!";
    }
}
