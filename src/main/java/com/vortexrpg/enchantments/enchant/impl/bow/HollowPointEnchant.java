package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;

import java.util.List;

/**
 * Hollow Point: +60%/70%/80% to unarmored, -30%/-35%/-40% to armored targets.
 */
public class HollowPointEnchant extends VortexEnchant {

    private static final double[] UNARMORED_BONUS = {0.60, 0.70, 0.80};
    private static final double[] ARMORED_PENALTY = {0.30, 0.35, 0.40};

    public HollowPointEnchant() {
        super("hollow_point", "Hollow Point", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        int armorThreshold = cfgi("armor_threshold", 5);
        int armorPoints = getArmorPoints(victim);

        if (armorPoints == 0) {
            event.setDamage(event.getDamage() * (1.0 + cfg("unarmored_bonus", UNARMORED_BONUS[level - 1])));
        } else if (armorPoints >= armorThreshold) {
            event.setDamage(event.getDamage() * (1.0 - cfg("armored_penalty", ARMORED_PENALTY[level - 1])));
        }
    }

    private int getArmorPoints(LivingEntity entity) {
        EntityEquipment eq = entity.getEquipment();
        if (eq == null) return 0;
        int count = 0;
        if (eq.getHelmet() != null && !eq.getHelmet().getType().isAir()) count++;
        if (eq.getChestplate() != null && !eq.getChestplate().getType().isAir()) count++;
        if (eq.getLeggings() != null && !eq.getLeggings().getType().isAir()) count++;
        if (eq.getBoots() != null && !eq.getBoots().getType().isAir()) count++;
        return count;
    }

    @Override
    public String getDescription() { return "Bonus damage to unarmored foes; reduced against armored."; }

    @Override
    public String getDescription(int level) {
        return "§7Unarmored: §a+" + (int)(UNARMORED_BONUS[level-1]*100) + "%§7. " +
               "§7Armored: §c-" + (int)(ARMORED_PENALTY[level-1]*100) + "%§7.";
    }
}
