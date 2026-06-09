package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Nature's Wrath: Hoe as weapon deals massive radial vine/thorn damage. */
public class NaturesWrathEnchant extends VortexEnchant {

    public NaturesWrathEnchant() { super("natures_wrath", "Nature's Wrath", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;
        double radius = cfg("radius", 4.0 + level);
        double aoe = cfg("aoe-damage", 3.0 + level * 2);
        int duration = cfgi("poison-duration", 40 + level * 20);
        var loc = victim.getLocation();
        for (LivingEntity e : MathUtil.getNearbyLiving(loc, radius)) {
            if (e.equals(attacker)) continue;
            e.damage(aoe, attacker);
            e.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, level - 1));
        }
        ParticleUtil.drawCircle(loc, radius, 40, Particle.HAPPY_VILLAGER);
        SoundUtil.play(loc, Sound.BLOCK_SWEET_BERRY_BUSH_BREAK, 1.0f, 0.5f);
        setCooldownFromConfig(attacker, "cooldown", 8);
    }

    @Override public String getDescription() { return "Attacks deal AoE nature damage."; }
    @Override public String getDescription(int level) {
        return "§7Attack: AoE §c" + (int)(3 + level * 2) + "♥§7 + §2Poison " + level + "§7 in " + (int)(4 + level) + "b."; }
}
