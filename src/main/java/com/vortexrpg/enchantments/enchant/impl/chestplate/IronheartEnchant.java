package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Ironheart: Increases max HP by 2/4/6 hearts passively. */
public class IronheartEnchant extends VortexEnchant {
    private static final double[] BONUS_HP = {4.0, 8.0, 12.0};

    public IronheartEnchant() { super("ironheart", "Ironheart", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        // Use absorption as a proxy for bonus HP display
        double stored = plugin.getPlayerDataManager().getIronheartAbsorption(player.getUniqueId());
        double bonus = cfg("bonus_hp", BONUS_HP[level-1]);
        if (stored != bonus) {
            plugin.getPlayerDataManager().setIronheartAbsorption(player.getUniqueId(), (int) bonus);
        }
        // Grant absorption if low (visual indicator of bonus HP)
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 100 == 0) {
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.ABSORPTION, 120, (int)(bonus/4)-1, true, false, false));
        }
    }

    @Override public String getDescription() { return "Passively gain bonus absorption hearts."; }
    @Override public String getDescription(int level) {
        return "§7Passive §a+" + (BONUS_HP[level-1]/2) + "§7 absorption hearts."; }
}
