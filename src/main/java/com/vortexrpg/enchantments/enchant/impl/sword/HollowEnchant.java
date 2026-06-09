package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * On hit, suppresses ALL enchantment effects on target's gear for 3/4/5 seconds.
 * Implementation: store enchants in PDC temp cache,
 * remove from items, restore after duration.
 */
public class HollowEnchant extends VortexEnchant {

    private static final int[] DURATIONS = {60, 80, 100}; // ticks

    public HollowEnchant() {
        super("hollow", "Hollow", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double chance = cfg("chance_percent", 100.0);
        if (!MathUtil.chance(chance)) return;

        int durationTicks = DURATIONS[level - 1];
        durationTicks = (int) MathUtil.secondsToTicks(cfg("suppress_duration_seconds", durationTicks / 20.0));

        if (!(victim instanceof Player targetPlayer)) return;

        EntityEquipment eq = targetPlayer.getEquipment();
        if (eq == null) return;

        List<ItemStack> items = List.of(
            eq.getHelmet(), eq.getChestplate(), eq.getLeggings(), eq.getBoots(),
            eq.getItemInMainHand(), eq.getItemInOffHand()
        );

        // Store enchants and remove them
        Map<Integer, Map<Enchantment, Integer>> savedEnchants = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            if (item == null || item.getType() == org.bukkit.Material.AIR) continue;
            Map<Enchantment, Integer> enchs = new HashMap<>(item.getEnchantments());
            if (!enchs.isEmpty()) {
                savedEnchants.put(i, enchs);
                enchs.keySet().forEach(item::removeEnchantment);
            }
        }

        // Also suppress VortexEnchantments on their gear (mark suppressed in PDC on player)
        NamespacedKey suppressKey = new NamespacedKey(plugin, "ve_suppressed");
        targetPlayer.getPersistentDataContainer().set(suppressKey, PersistentDataType.BYTE, (byte) 1);

        ParticleUtil.spawn(targetPlayer.getLocation().add(0, 1, 0), Particle.ENCHANT, 12, 0.4);
        SoundUtil.play(targetPlayer.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 0.5f);

        final int dur = durationTicks;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!targetPlayer.isOnline() || targetPlayer.isDead()) return;
            // Restore vanilla enchants
            for (Map.Entry<Integer, Map<Enchantment, Integer>> entry : savedEnchants.entrySet()) {
                ItemStack restored = getItemAtSlot(targetPlayer, entry.getKey());
                if (restored != null && restored.getType() != org.bukkit.Material.AIR) {
                    entry.getValue().forEach((e, l) -> restored.addEnchantment(e, l));
                }
            }
            // Remove suppression flag
            targetPlayer.getPersistentDataContainer().remove(suppressKey);
        }, dur);
    }

    private ItemStack getItemAtSlot(Player player, int slotIndex) {
        EntityEquipment eq = player.getEquipment();
        if (eq == null) return null;
        return switch (slotIndex) {
            case 0 -> eq.getHelmet();
            case 1 -> eq.getChestplate();
            case 2 -> eq.getLeggings();
            case 3 -> eq.getBoots();
            case 4 -> eq.getItemInMainHand();
            case 5 -> eq.getItemInOffHand();
            default -> null;
        };
    }

    @Override
    public String getDescription() { return "On hit, suppresses all enchantment effects on target's gear temporarily."; }

    @Override
    public String getDescription(int level) {
        int[] secs = {3, 4, 5};
        return "Suppress all enchants on target's gear for §c" + secs[level - 1] + "s§7.";
    }
}
