package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Mud Trap: Right-click dirt to create slowness zone. */
public class MudTrapEnchant extends VortexEnchant {
    private static final int[] DURATION = {60, 100, 160};

    public MudTrapEnchant() { super("mud_trap", "Mud Trap", EnchantRarity.RARE, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getClickedBlock() == null) return;
        Material mat = event.getClickedBlock().getType();
        if (mat != Material.DIRT && mat != Material.GRASS_BLOCK) return;
        if (isOnCooldown(player)) return;
        setCooldownFromConfig(player, "cooldown", 10);
        double radius = cfg("radius", 3.0);
        int ticks = cfgi("duration_ticks", DURATION[level - 1]);
        for (LivingEntity e : MathUtil.getNearbyLiving(event.getClickedBlock().getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 2, true, false));
        }
        // Convert to mud visually
        Block center = event.getClickedBlock();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block b = center.getRelative(x, 0, z);
                if (b.getType() == Material.DIRT || b.getType() == Material.GRASS_BLOCK) {
                    b.setType(Material.MUD);
                }
            }
        }
    }

    @Override public String getDescription() { return "Right-click dirt to create mud trap."; }
    @Override public String getDescription(int level) {
        return "§7Right-click dirt: §cSlowness III§7 zone for " + (DURATION[level - 1] / 20) + "s."; }
}
