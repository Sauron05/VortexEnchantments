package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/** StormWing: In rain, lightning strikes a random nearby enemy while gliding. */
public class StormWingEnchant extends VortexEnchant {

    public StormWingEnchant() { super("storm_wing", "Storm Wing", EnchantRarity.EPIC, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        if (!player.getWorld().hasStorm()) return;
        int interval = cfgi("interval", Math.max(2, 6 - level));
        int ticks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "sw_t", 0) + 1;
        if (ticks >= interval) {
            ticks = 0;
            double radius = cfgd("radius", 15.0);
            List<LivingEntity> targets = new ArrayList<>();
            for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
                if (e != player && e instanceof LivingEntity le && !(le instanceof Player)) targets.add(le);
            }
            if (!targets.isEmpty()) {
                LivingEntity target = targets.get((int) (Math.random() * targets.size()));
                player.getWorld().strikeLightningEffect(target.getLocation());
                target.damage(cfgd("damage", 4.0 + level), player);
            }
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "sw_t", ticks);
    }

    @Override public String getDescription() { return "Lightning strikes enemies during storms."; }
    @Override public String getDescription(int level) {
        return "§7In rain, §elightning§7 strikes a random mob every §e" + Math.max(2, 6 - level) + "s§7 while gliding."; }
}
