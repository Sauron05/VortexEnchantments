package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Soul Harvest: Killing entities generates "soul charges" (max 5/7/10).
 * Right-click to consume all charges, healing 1 heart per charge.
 */
public class SoulHarvestEnchant extends VortexEnchant {

    public SoulHarvestEnchant() {
        super("soul_harvest", "Soul Harvest", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;

        int maxCharges = cfgi("max_charges", 3 + level * 2);
        String key = "soul_harvest_charges";

        int charges = plugin.getPlayerDataManager().getInt(killer.getUniqueId(), key);
        if (charges < maxCharges) {
            charges++;
            plugin.getPlayerDataManager().setInt(killer.getUniqueId(), key, charges);
            ParticleUtil.spawn(killed.getLocation().add(0, 1, 0), Particle.SOUL, 10, 0.3);
            killer.sendMessage("§5[Soul Harvest] §7+1 soul charge (§e" + charges + "/" + maxCharges + "§7)");
        }
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, ItemStack item, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;

        String key = "soul_harvest_charges";
        int charges = plugin.getPlayerDataManager().getInt(player.getUniqueId(), key);
        if (charges <= 0) return;

        double healPerCharge = cfgd("heal_per_charge", 2.0);
        double totalHeal = charges * healPerCharge;

        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(maxHealth, player.getHealth() + totalHeal));
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, 0);

        ParticleUtil.spawnHelix(player.getLocation(), Particle.SOUL, 3, 2.0);
        SoundUtil.play(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.8f, 1.5f);
        player.sendMessage("§5[Soul Harvest] §7Consumed §e" + charges + " §7souls! Healed §a"
            + String.format("%.1f", totalHeal / 2) + "\u2764§7.");
    }

    @Override
    public String getDescription(int level) {
        int max = 3 + level * 2;
        return "§7Kills store §5soul charges§7 (max §e" + max + "§7). Right-click to heal §a1\u2764§7 per charge.";
    }
}
