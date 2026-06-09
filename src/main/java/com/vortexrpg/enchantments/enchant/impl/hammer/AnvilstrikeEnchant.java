package com.vortexrpg.enchantments.enchant.impl.hammer;

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
 * Anvilstrike: Bonus damage scales with fall distance — +15/20/25% per block fallen (max 10 blocks).
 * Midair slam mechanic for the mace.
 */
public class AnvilstrikeEnchant extends VortexEnchant {

    public AnvilstrikeEnchant() {
        super("anvilstrike", "Anvilstrike", EnchantRarity.RARE, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        float fallDist = attacker.getFallDistance();
        if (fallDist < 1.0f) return;

        int maxBlocks = cfgi("max_blocks", 10);
        double perBlock = cfgd("per_block", 0.10 + level * 0.05);
        double capped = Math.min(fallDist, maxBlocks);
        double bonus = capped * perBlock;

        event.setDamage(event.getDamage() * (1.0 + bonus));

        ParticleUtil.drawCircle(victim.getLocation(), 2.0, 12, Particle.DUST_PLUME);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7f, 0.8f);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.05) * 100);
        return "§7+" + pct + "% dmg per block fallen §8(max 10). §7Aerial slam!";
    }
}
