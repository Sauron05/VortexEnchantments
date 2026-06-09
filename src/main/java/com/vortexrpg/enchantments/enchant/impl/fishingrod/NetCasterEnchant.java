package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** NetCaster: Caught fish scatter into multiple smaller drops. */
public class NetCasterEnchant extends VortexEnchant {

    public NetCasterEnchant() { super("net_caster", "Net Caster", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item)) return;
        int extraDrops = cfgi("extra_drops", 1 + level);
        Material[] fish = {Material.COD, Material.SALMON, Material.TROPICAL_FISH, Material.PUFFERFISH};
        for (int i = 0; i < extraDrops; i++) {
            Material mat = fish[(int) (Math.random() * fish.length)];
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(mat));
        }
    }

    @Override public String getDescription() { return "Catches scatter into extra fish drops."; }
    @Override public String getDescription(int level) {
        return "§7Each catch also drops §a" + (1 + level) + "§7 extra random fish."; }
}
