package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** SkySovereign: Complete aerial mastery — buffs allies, debuffs enemies, regen+resist. */
public class SkySovereignEnchant extends VortexEnchant {

    public SkySovereignEnchant() { super("sky_sovereign", "Sky Sovereign", EnchantRarity.MYTHIC, 1, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        double radius = cfgd("radius", 12.0);
        // Self buffs
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 1, true, false, true));
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 5, 1, 1, 1, 0.02);
        for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
            if (e == player) continue;
            if (e instanceof Player ally) {
                ally.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0, true, false, true));
                ally.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0, true, false, true));
            } else if (e instanceof LivingEntity le) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 1, true, false));
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 1, true, false));
            }
        }
    }

    @Override public String getDescription() { return "Total aerial dominance — buff allies, cripple foes."; }
    @Override public String getDescription(int level) {
        return "§dComplete mastery§7: Regen II + Resist II, allies buffed, enemies §cdebuffed§7 in §e12§7 blocks."; }
}
