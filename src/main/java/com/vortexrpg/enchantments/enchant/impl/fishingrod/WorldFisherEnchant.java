package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** WorldFisher: Can fish from any location; catches include dimensional loot. */
public class WorldFisherEnchant extends VortexEnchant {
    private static final Material[] DIMENSIONAL_LOOT = {
        Material.ENDER_PEARL, Material.BLAZE_ROD, Material.GHAST_TEAR,
        Material.CHORUS_FRUIT, Material.NETHERITE_SCRAP, Material.SHULKER_SHELL,
        Material.NETHER_STAR, Material.DRAGON_BREATH
    };

    public WorldFisherEnchant() { super("world_fisher", "World Fisher", EnchantRarity.MYTHIC, 1, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        // Bonus dimensional loot
        if (Math.random() < cfgd("dimensional_chance", 0.20)) {
            Material mat = DIMENSIONAL_LOOT[(int) (Math.random() * DIMENSIONAL_LOOT.length)];
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(mat));
        }
        event.setExpToDrop(event.getExpToDrop() * 3);
    }

    @Override public String getDescription() { return "Fish across dimensions for exotic loot."; }
    @Override public String getDescription(int level) {
        return "§d20%§7 chance for §5dimensional loot§7 (ender pearls, netherite, dragon breath) + §a3x XP§7."; }
}
