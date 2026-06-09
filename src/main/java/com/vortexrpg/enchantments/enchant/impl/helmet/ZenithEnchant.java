package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

/**
 * Zenith: Sneak to mark your position. Sneak again to teleport back + heal X%.
 * 30s CD.
 */
public class ZenithEnchant extends VortexEnchant {
    private static final java.util.Map<java.util.UUID, org.bukkit.Location> MARKS = new java.util.HashMap<>();

    public ZenithEnchant() { super("zenith", "Zenith", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;

        java.util.UUID id = player.getUniqueId();
        if (MARKS.containsKey(id)) {
            org.bukkit.Location mark = MARKS.remove(id);
            ParticleUtil.spawn(player.getLocation(), Particle.PORTAL, 30, 0.5);
            player.teleport(mark);
            double healPct = cfgd("heal_pct", 0.10 + level * 0.05);
            double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
            player.setHealth(Math.min(maxHp, player.getHealth() + maxHp * healPct));
            ParticleUtil.spawn(mark, Particle.PORTAL, 30, 0.5);
            SoundUtil.play(mark, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
            setCooldownFromConfig(player, "cooldown", 30.0);
        } else {
            MARKS.put(id, player.getLocation().clone());
            player.sendActionBar(net.kyori.adventure.text.Component.text("§d§lZenith §7mark set!"));
            SoundUtil.play(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.5f);
        }
    }

    @Override public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.05) * 100);
        return "§7Sneak: set mark. Sneak again: teleport back + heal §a" + pct + "%§7. §830s CD.";
    }
}
