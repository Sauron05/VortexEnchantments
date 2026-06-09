package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Celestial: Slow regeneration while gliding above Y=200. */
public class CelestialEnchant extends VortexEnchant {

    public CelestialEnchant() { super("celestial", "Celestial", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        double minY = cfgd("min_y", 200.0);
        if (player.getLocation().getY() < minY) return;
        int interval = cfgi("heal_interval", Math.max(1, 4 - level));
        int ticks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "celestial_t", 0) + 1;
        if (ticks >= interval) {
            ticks = 0;
            double heal = cfgd("heal", 1.0);
            double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
            player.setHealth(Math.min(player.getHealth() + heal, maxHp));
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "celestial_t", ticks);
    }

    @Override public String getDescription() { return "Regenerate health in the upper skies."; }
    @Override public String getDescription(int level) {
        return "§7Heal §a1\u2764§7 every §e" + Math.max(1, 4 - level) + "s§7 while gliding above §eY=200§7."; }
}
