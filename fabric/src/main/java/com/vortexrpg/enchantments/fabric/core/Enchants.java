package com.vortexrpg.enchantments.fabric.core;

import com.vortexrpg.enchantments.fabric.enchant.sword.DebtEnchant;
import com.vortexrpg.enchantments.fabric.enchant.sword.DormantEnchant;
import com.vortexrpg.enchantments.fabric.enchant.sword.DroughtEnchant;
import com.vortexrpg.enchantments.fabric.enchant.sword.MimicEnchant;
import com.vortexrpg.enchantments.fabric.enchant.sword.ThirstEnchant;

/**
 * Registers every native-Fabric enchantment in the same order as the Paper edition.
 *
 * <p>Batch 1 (sword) is live; the remaining enchantments toward the first 200 are added here as
 * each batch is ported.
 */
public final class Enchants {

    private Enchants() {}

    public static void registerAll() {
        // ── Sword (batch 1) ───────────────────────────────────────────────
        EnchantRegistry.register(new DebtEnchant());
        EnchantRegistry.register(new ThirstEnchant());
        EnchantRegistry.register(new DormantEnchant());
        EnchantRegistry.register(new DroughtEnchant());
        EnchantRegistry.register(new MimicEnchant());
    }
}
