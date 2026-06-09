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

import java.util.List;

/**
 * Ouroboros: On kill, heal to full health but permanently lose 1 heart
 * of max health (until death and respawn). Risk/reward cycle.
 * Max stacks: 3/5/7 per level (to prevent going below 2 hearts).
 */
public class OuroborosEnchant extends VortexEnchant {

    public OuroborosEnchant() {
        super("ouroboros", "Ouroboros", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;

        int maxReductions = cfgi("max_reductions", 1 + level * 2);
        double reductionPerKill = cfgd("reduction_per_kill", 2.0);
        double minMaxHealth = cfgd("min_max_health", 4.0);
        String dataKey = "ouroboros_stacks";

        int currentStacks = plugin.getPlayerDataManager().getInt(killer.getUniqueId(), dataKey);
        if (currentStacks >= maxReductions) {
            killer.sendMessage("§4[Ouroboros] §7Max reductions reached! No more sacrifices.");
            return;
        }

        var attr = killer.getAttribute(Attribute.MAX_HEALTH);
        if (attr == null) return;

        double currentMax = attr.getBaseValue();
        if (currentMax - reductionPerKill < minMaxHealth) {
            killer.sendMessage("§4[Ouroboros] §7Too dangerous! Health too low.");
            return;
        }

        attr.setBaseValue(currentMax - reductionPerKill);
        killer.setHealth(attr.getBaseValue());
        plugin.getPlayerDataManager().setInt(killer.getUniqueId(), dataKey, currentStacks + 1);

        ParticleUtil.spawnHelix(killer.getLocation(), Particle.SOUL_FIRE_FLAME, 3, 2.0);
        SoundUtil.play(killer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.5f, 2.0f);
        killer.sendMessage("§4[Ouroboros] §7Full heal! But max HP reduced by §c1\u2764§7. (§e" +
            (currentStacks + 1) + "/" + maxReductions + " stacks§7)");
    }

    @Override
    public void onRespawn(Player player, int level) {
        String dataKey = "ouroboros_stacks";
        int stacks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), dataKey);
        if (stacks > 0) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), dataKey, 0);
            var attr = player.getAttribute(Attribute.MAX_HEALTH);
            if (attr != null) {
                attr.setBaseValue(20.0);
            }
            player.sendMessage("§4[Ouroboros] §7The cycle resets. Max health restored.");
        }
    }

    @Override
    public String getDescription(int level) {
        int maxStacks = 1 + level * 2;
        return "§7On kill: §afull heal§7, but lose §c1\u2764§7 max HP. Resets on death. §8(max " + maxStacks + " stacks)";
    }
}
