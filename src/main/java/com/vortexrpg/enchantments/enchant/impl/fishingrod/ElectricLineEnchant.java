package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.List;

/** ElectricLine: Hooked entities take shock damage. */
public class ElectricLineEnchant extends VortexEnchant {
    private static final double[] DMG = {2.0, 3.0, 4.0};

    public ElectricLineEnchant() { super("electric_line", "Electric Line", EnchantRarity.RARE, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) return;
        if (!(event.getCaught() instanceof LivingEntity target)) return;
        double damage = cfgd("damage", DMG[level - 1]);
        target.damage(damage, player);
        target.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, target.getLocation().add(0, 1, 0), 15, 0.3, 0.5, 0.3, 0.05);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.6f, 1.5f);
    }

    @Override public String getDescription() { return "Shock damage to hooked entities."; }
    @Override public String getDescription(int level) {
        return "§7Hooked entities take §e" + (int) DMG[level - 1] + "§7 shock damage."; }
}
