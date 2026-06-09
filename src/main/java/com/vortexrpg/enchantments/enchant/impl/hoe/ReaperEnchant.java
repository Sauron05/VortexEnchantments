package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.Set;

/** Reaper: +150/175/200% damage to undead mobs when using hoe as weapon. */
public class ReaperEnchant extends VortexEnchant {
    private static final double[] BONUS = {1.50, 1.75, 2.00};

    public ReaperEnchant() { super("reaper_hoe", "Reaper", EnchantRarity.EPIC, 3, List.of(ItemTarget.HOE)); }

    private static final Set<EntityType> UNDEAD = Set.of(
        EntityType.ZOMBIE, EntityType.SKELETON, EntityType.WITHER_SKELETON,
        EntityType.HUSK, EntityType.STRAY, EntityType.PHANTOM, EntityType.DROWNED,
        EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN, EntityType.SKELETON_HORSE,
        EntityType.ZOMBIE_HORSE, EntityType.WITHER
    );

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (UNDEAD.contains(target.getType())) {
            double bonus = cfg("undead_bonus", BONUS[level-1]);
            event.setDamage(event.getDamage() * (1.0 + bonus));
        }
    }

    @Override public String getDescription() { return "Greatly increases damage to undead."; }
    @Override public String getDescription(int level) {
        return "§7+" + (int)(BONUS[level-1]*100) + "§a%§7 damage to undead mobs."; }
}
