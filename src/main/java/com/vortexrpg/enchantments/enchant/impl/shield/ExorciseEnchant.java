package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Set;

/** Exorcise: Blocking undead mobs deals bonus damage (Smite on block). */
public class ExorciseEnchant extends VortexEnchant {
    private static final double[] BONUS = {1.0, 2.0, 3.0};
    private static final Set<EntityType> UNDEAD = Set.of(
        EntityType.ZOMBIE, EntityType.SKELETON, EntityType.WITHER_SKELETON,
        EntityType.PHANTOM, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER
    );

    public ExorciseEnchant() { super("exorcise", "Exorcise", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled() || !player.isBlocking()) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        if (UNDEAD.contains(attacker.getType())) {
            double bonus = cfg("bonus", BONUS[level-1]);
            attacker.damage(bonus, player);
            attacker.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0));
        }
    }

    @Override public String getDescription() { return "Blocking undead mobs damages them."; }
    @Override public String getDescription(int level) {
        return "§7Block undead hits: deal §a" + BONUS[level-1] + "§7 bonus damage back."; }
}
