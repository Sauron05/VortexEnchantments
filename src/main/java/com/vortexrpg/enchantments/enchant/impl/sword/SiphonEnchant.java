package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Siphon: Steals 1 random positive potion effect from target, applying it to attacker.
 */
@SuppressWarnings("deprecation")
public class SiphonEnchant extends VortexEnchant {

    private static final Set<PotionEffectType> POSITIVE_EFFECTS = Set.of(
        PotionEffectType.SPEED, PotionEffectType.HASTE, PotionEffectType.STRENGTH,
        PotionEffectType.JUMP_BOOST, PotionEffectType.REGENERATION, PotionEffectType.RESISTANCE,
        PotionEffectType.FIRE_RESISTANCE, PotionEffectType.WATER_BREATHING, PotionEffectType.INVISIBILITY,
        PotionEffectType.NIGHT_VISION, PotionEffectType.HEALTH_BOOST, PotionEffectType.ABSORPTION,
        PotionEffectType.SATURATION, PotionEffectType.DOLPHINS_GRACE, PotionEffectType.CONDUIT_POWER,
        PotionEffectType.LUCK, PotionEffectType.SLOW_FALLING, PotionEffectType.HERO_OF_THE_VILLAGE
    );

    public SiphonEnchant() {
        super("siphon", "Siphon", EnchantRarity.LEGENDARY, 1, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double chance = cfg("chance_percent", 100.0);
        if (!MathUtil.chance(chance)) return;

        boolean onlyPositive = cfgb("only_positive_effects", true);
        List<PotionEffect> applicable = new ArrayList<>();
        for (PotionEffect pe : victim.getActivePotionEffects()) {
            if (!onlyPositive || POSITIVE_EFFECTS.contains(pe.getType())) {
                applicable.add(pe);
            }
        }
        if (applicable.isEmpty()) return;

        PotionEffect stolen = applicable.get(new Random().nextInt(applicable.size()));
        victim.removePotionEffect(stolen.getType());
        attacker.addPotionEffect(stolen);
        attacker.sendMessage("§5[Siphon] §7Stole §d" + stolen.getType().getName() + "§7 from target!");
    }

    @Override
    public String getDescription() { return "Steals a random positive potion effect from the target."; }

    @Override
    public String getDescription(int level) {
        return "§7Steals §d1 random positive effect§7 from the target and applies it to you.";
    }
}
