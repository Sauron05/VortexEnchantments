package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Harvest: On kill, all food items in inventory gain +0.5/+1/+1.5 saturation bonus (lore + PDC).
 * Cap +4 per food item.
 */
@SuppressWarnings("deprecation")
public class HarvestEnchant extends VortexEnchant {

    private static final double[] SAT_PER_KILL = {0.5, 1.0, 1.5};
    public static final double MAX_SAT_BONUS = 4.0;

    public HarvestEnchant() {
        super("harvest", "Harvest", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        double bonusPerKill = cfg("saturation_per_kill", SAT_PER_KILL[level - 1]);
        double maxBonus = cfg("max_saturation_bonus", MAX_SAT_BONUS);
        NamespacedKey satKey = new NamespacedKey(plugin, "harvest_sat_bonus");

        for (ItemStack item : killer.getInventory().getContents()) {
            if (item == null || !item.getType().isEdible()) continue;
            var meta = item.getItemMeta();
            if (meta == null) continue;

            double current = meta.getPersistentDataContainer()
                .getOrDefault(satKey, PersistentDataType.DOUBLE, 0.0);
            double newBonus = Math.min(current + bonusPerKill, maxBonus);
            meta.getPersistentDataContainer().set(satKey, PersistentDataType.DOUBLE, newBonus);

            // Update lore
            var lore = meta.getLore() != null ? meta.getLore() : new java.util.ArrayList<String>();
            lore.removeIf(l -> l.contains("Harvest Bonus"));
            if (newBonus > 0) {
                lore.add("§a+§6" + String.format("%.1f", newBonus) + "§a Harvest Bonus Saturation");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    @Override
    public String getDescription() { return "Killing enemies adds saturation bonus to food in your inventory."; }

    @Override
    public String getDescription(int level) {
        return "§7Each kill: food items gain §a+" + SAT_PER_KILL[level - 1] + "§7 saturation (cap §a+" + MAX_SAT_BONUS + "§7).";
    }
}
