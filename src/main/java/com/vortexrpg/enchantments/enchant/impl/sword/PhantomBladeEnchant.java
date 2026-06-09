package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Phantom Blade: 20/30/40% chance for attacks to bypass shields entirely.
 * Ignores the blocking state of the target.
 */
public class PhantomBladeEnchant extends VortexEnchant {

    public PhantomBladeEnchant() {
        super("phantom_blade", "Phantom Blade", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double chance = cfgd("bypass_chance", 0.1 + level * 0.1);

        if (victim instanceof Player target && target.isBlocking()) {
            if (MathUtil.chance(chance * 100)) {
                event.setDamage(event.getDamage());
                target.setCooldown(target.getActiveItem().getType(), 0);

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (target.isOnline() && target.isValid()) {
                        target.damage(event.getDamage(), attacker);
                    }
                });

                attacker.sendMessage("§5[Phantom] §7Your blade passed through the shield!");
                target.sendMessage("§5[Phantom] §7Your shield was §cpierced§7!");
            }
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.1 + level * 0.1) * 100);
        return "§7" + pct + "% chance to §5bypass shields§7 entirely.";
    }
}
