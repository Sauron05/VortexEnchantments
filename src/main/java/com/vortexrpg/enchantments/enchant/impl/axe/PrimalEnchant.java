package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.block.Biome;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.Set;

/**
 * Primal: Forest/jungle/taiga biomes = +20%/25%/30% damage. Desert/ocean/badlands = -10%.
 */
public class PrimalEnchant extends VortexEnchant {

    private static final double[] DAMAGE_BONUS = {0.20, 0.25, 0.30};

    private static final Set<Biome> BUFF_BIOMES = Set.of(
        Biome.FOREST, Biome.FLOWER_FOREST, Biome.BIRCH_FOREST, Biome.OLD_GROWTH_BIRCH_FOREST,
        Biome.DARK_FOREST, Biome.JUNGLE, Biome.SPARSE_JUNGLE, Biome.BAMBOO_JUNGLE,
        Biome.TAIGA, Biome.OLD_GROWTH_SPRUCE_TAIGA, Biome.OLD_GROWTH_PINE_TAIGA,
        Biome.SNOWY_TAIGA, Biome.GROVE
    );

    private static final Set<Biome> NERF_BIOMES = Set.of(
        Biome.DESERT, Biome.OCEAN, Biome.DEEP_OCEAN, Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN,
        Biome.FROZEN_OCEAN, Biome.DEEP_FROZEN_OCEAN, Biome.LUKEWARM_OCEAN, Biome.DEEP_LUKEWARM_OCEAN,
        Biome.WARM_OCEAN, Biome.BADLANDS, Biome.ERODED_BADLANDS, Biome.WOODED_BADLANDS
    );

    public PrimalEnchant() {
        super("primal", "Primal", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        Biome biome = attacker.getLocation().getBlock().getBiome();

        if (BUFF_BIOMES.contains(biome)) {
            event.setDamage(event.getDamage() * (1.0 + cfg("damage_bonus", DAMAGE_BONUS[level - 1])));
        } else if (NERF_BIOMES.contains(biome)) {
            event.setDamage(event.getDamage() * (1.0 - cfg("damage_penalty", 0.10)));
        }
    }

    @Override
    public String getDescription() { return "Boosts damage in natural biomes, penalizes in hostile ones."; }

    @Override
    public String getDescription(int level) {
        return "§7Forest/jungle/taiga: §a+" + (int)(DAMAGE_BONUS[level-1]*100) + "%§7 dmg. Desert/ocean: §c-10%§7 dmg.";
    }
}
