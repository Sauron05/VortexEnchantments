package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Shieldbreaker: Attacks bypass the target's shield entirely.
 * Also disables the opponent's shield for a short time.
 */
public class ShieldbreakerEnchant extends VortexEnchant {

    public ShieldbreakerEnchant() {
        super("shieldbreaker", "Shieldbreaker", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!(victim instanceof Player target)) return;

        if (target.isBlocking()) {
            event.setCancelled(false);
            int cdTicks = cfgi("shield_cd_ticks", 60 + level * 20);
            target.setCooldown(org.bukkit.Material.SHIELD, cdTicks);

            ParticleUtil.spawn(target.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 15, 0.4);
            SoundUtil.play(target.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 0.6f);
            target.sendMessage("§c[Shieldbreaker] §7Your shield was bypassed!");
        }
    }

    @Override
    public String getDescription(int level) {
        double secs = (60 + level * 20) / 20.0;
        return "§7Attacks §cbypass shields §7and disable them for §e" + secs + "s§7.";
    }
}
