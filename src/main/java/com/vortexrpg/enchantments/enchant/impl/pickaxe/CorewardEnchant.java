package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/** Coreward: Mining speed +2/3/4% per block below Y=64. */
@SuppressWarnings("removal")
public class CorewardEnchant extends VortexEnchant {
    private static final double[] BONUS = {0.02, 0.03, 0.04};
    private static final UUID MOD_UUID = UUID.fromString("b3c4e5f6-1234-5678-9abc-def012345678");

    public CorewardEnchant() { super("coreward", "Coreward", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        int refY = cfgi("reference_y", 64);
        int y = player.getLocation().getBlockY();
        if (y >= refY) return;
        int depth = refY - y;
        double bonus = cfg("bonus_per_block", BONUS[level-1]) * depth;
        AttributeInstance attr = player.getAttribute(Attribute.MINING_EFFICIENCY);
        if (attr == null) return;
        attr.getModifiers().stream()
            .filter(m -> m.getUniqueId().equals(MOD_UUID))
            .findFirst().ifPresent(attr::removeModifier);
        AttributeModifier mod = new AttributeModifier(MOD_UUID, "coreward_bonus", bonus, AttributeModifier.Operation.ADD_NUMBER);
        attr.addModifier(mod);
    }

    @Override public String getDescription() { return "Mining speed scales with depth."; }
    @Override public String getDescription(int level) {
        return "§a+" + (int)(BONUS[level-1]*100) + "%§7 mining speed per block below §eY=64§7."; }
}
