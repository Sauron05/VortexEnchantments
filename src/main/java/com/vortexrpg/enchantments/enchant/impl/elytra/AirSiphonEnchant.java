package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/** AirSiphon: Heal while gliding near mobs. */
public class AirSiphonEnchant extends VortexEnchant {
    private static final double[] HEAL = {0.5, 1.0, 1.5};

    public AirSiphonEnchant() { super("air_siphon", "Air Siphon", EnchantRarity.RARE, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        int interval = cfgi("interval", 3);
        int ticks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "air_siphon_t", 0) + 1;
        if (ticks >= interval) {
            ticks = 0;
            double radius = cfgd("radius", 5.0);
            boolean found = false;
            for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
                if (e != player && e instanceof LivingEntity) { found = true; break; }
            }
            if (found) {
                double heal = cfgd("heal", HEAL[level - 1]);
                double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                player.setHealth(Math.min(player.getHealth() + heal, maxHp));
            }
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "air_siphon_t", ticks);
    }

    @Override public String getDescription() { return "Drain life from mobs while flying near them."; }
    @Override public String getDescription(int level) {
        return "§7Heal §a" + HEAL[level - 1] + "§c\u2764§7 every §e3s§7 while gliding near mobs."; }
}
