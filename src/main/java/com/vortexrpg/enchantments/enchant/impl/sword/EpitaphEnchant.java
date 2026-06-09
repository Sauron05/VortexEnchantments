package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ItemUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Epitaph: On killing a named mob or player, permanently records the kill in sword lore.
 * After 10 recorded kills, gain permanent +5% damage on that specific sword.
 */
@SuppressWarnings("deprecation")
public class EpitaphEnchant extends VortexEnchant {

    public EpitaphEnchant() {
        super("epitaph", "Epitaph", EnchantRarity.MYTHIC, 1, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;

        String killName = null;
        if (killed instanceof Player targetPlayer) {
            killName = targetPlayer.getName();
        } else if (killed.getCustomName() != null) {
            killName = killed.getCustomName();
        }
        if (killName == null) return;

        ItemStack sword = killer.getInventory().getItemInMainHand();
        if (ItemUtil.isAir(sword)) return;

        int maxEntries = cfgi("max_lore_entries", 20);
        int killsForBonus = cfgi("kills_for_bonus", 10);
        double bonusDmgPct = cfg("bonus_damage_percent", 5.0);

        NamespacedKey killCountKey = new NamespacedKey(plugin, "epitaph_kills");
        var meta = sword.getItemMeta();
        if (meta == null) return;

        var pdc = meta.getPersistentDataContainer();
        int killCount = pdc.getOrDefault(killCountKey, PersistentDataType.INTEGER, 0) + 1;
        pdc.set(killCountKey, PersistentDataType.INTEGER, killCount);

        // Build kill log entry
        String entry = "§8[Epitaph] §7" + killName + " §8(" + Instant.now().toString().substring(0, 10) + ")";

        // Update lore
        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        if (lore.size() < maxEntries + 5) { // +5 for enchant lines
            lore.add(entry);
        }
        meta.setLore(lore);

        // Store bonus damage in PDC once threshold reached
        if (killCount >= killsForBonus) {
            NamespacedKey bonusKey = new NamespacedKey(plugin, "epitaph_bonus");
            double existing = pdc.getOrDefault(bonusKey, PersistentDataType.DOUBLE, 0.0);
            if (existing == 0.0) {
                pdc.set(bonusKey, PersistentDataType.DOUBLE, bonusDmgPct / 100.0);
                lore.add("§6[Epitaph Mastery] §a+" + bonusDmgPct + "% §6damage");
                meta.setLore(lore);
                killer.sendMessage("§6[Epitaph] §aYour sword has earned the Epitaph bonus!");
            }
        }

        sword.setItemMeta(meta);
    }

    @Override
    public void onAttack(org.bukkit.event.entity.EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        ItemStack sword = attacker.getInventory().getItemInMainHand();
        if (ItemUtil.isAir(sword)) return;

        var meta = sword.getItemMeta();
        if (meta == null) return;
        NamespacedKey bonusKey = new NamespacedKey(plugin, "epitaph_bonus");
        double bonus = meta.getPersistentDataContainer().getOrDefault(bonusKey, PersistentDataType.DOUBLE, 0.0);
        if (bonus > 0) {
            event.setDamage(event.getDamage() * (1.0 + bonus));
        }
    }

    @Override
    public String getDescription() { return "Records kills in sword lore. 10 named kills grants permanent +5% damage."; }

    @Override
    public String getDescription(int level) {
        return "Kill named mobs/players to record in sword lore. After §e10 §7kills: §a+5% §7permanent damage.";
    }
}
