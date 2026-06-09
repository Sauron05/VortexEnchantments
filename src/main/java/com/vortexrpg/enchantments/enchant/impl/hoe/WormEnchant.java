package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/** Worm: Tilling has 8/10/12% chance to spawn a worm Slime; right-click to collect bone meal. */
public class WormEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.08, 0.10, 0.12};
    private static final NamespacedKey WORM_KEY = new NamespacedKey("vortexenchantments", "worm_entity");

    public WormEnchant() { super("worm", "Worm", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getClickedBlock() == null) return;
        var type = event.getClickedBlock().getType();
        if (type != Material.DIRT && type != Material.GRASS_BLOCK) return;
        double chance = cfg("chance", CHANCE[level-1]);
        if (Math.random() < chance) {
            Slime slime = (Slime) player.getWorld().spawnEntity(event.getClickedBlock().getLocation().add(0.5, 1, 0.5), EntityType.SLIME);
            slime.setSize(1);
            slime.setAI(false);
            slime.getPersistentDataContainer().set(WORM_KEY, PersistentDataType.BYTE, (byte) 1);
            slime.customName(net.kyori.adventure.text.Component.text("§aWorm"));
            slime.setCustomNameVisible(true);
        }
    }

    @Override public String getDescription() { return "Tilling may spawn a worm for bone meal."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level-1]*100) + "§a%§7 to spawn a worm slime when tilling."; }
}
