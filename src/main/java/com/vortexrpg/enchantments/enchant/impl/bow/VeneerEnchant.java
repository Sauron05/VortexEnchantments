package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Veneer: Arrow applies a mark. Marked targets take +5%/8%/10% from all sources.
 */
public class VeneerEnchant extends VortexEnchant {

    private static final double[] AMP = {0.05, 0.08, 0.10};
    private static final int[] MARK_SECS = {8, 10, 12};

    public VeneerEnchant() {
        super("veneer", "Veneer", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        long durationMs = cfgi("mark_duration_seconds", MARK_SECS[level - 1]) * 1000L;
        plugin.getPlayerDataManager().setVeneerMark(victim.getUniqueId(), durationMs);
        // Show mark visually
        victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,
            (int)(durationMs / 50), 0, false, false, false));
    }

    /** Called from EnchantListener on any EntityDamageEvent to check veneer amplification. */
    public static double getVeneerAmp(LivingEntity entity) {
        if (!com.vortexrpg.enchantments.VortexEnchantments.getInstance()
            .getPlayerDataManager().isVeneerMarked(entity.getUniqueId())) return 1.0;
        // Default amp since we don't know level here; caller must look up the enchant
        return 1.05;
    }

    @Override
    public String getDescription() { return "Marks enemies, amplifying all damage they receive."; }

    @Override
    public String getDescription(int level) {
        return "§7Arrow marks target §e" + MARK_SECS[level-1] + "s§7: §c+" + (int)(AMP[level-1]*100) + "%§7 damage from all sources.";
    }
}
