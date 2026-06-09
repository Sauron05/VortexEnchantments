package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** OceanSovereign: Permanent Conduit Power and triple catch rates near water. */
public class OceanSovereignEnchant extends VortexEnchant {

    public OceanSovereignEnchant() { super("ocean_sovereign", "Ocean Sovereign", EnchantRarity.MYTHIC, 1, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        boolean nearWater = player.getLocation().getBlock().getType() == org.bukkit.Material.WATER
                || player.getLocation().subtract(0, 1, 0).getBlock().getType() == org.bukkit.Material.WATER;
        if (nearWater) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 60, 0, true, false, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 1, true, false, true));
        }
    }

    @Override
    public void onFish(org.bukkit.event.player.PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != org.bukkit.event.player.PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof org.bukkit.entity.Item caught)) return;
        // Triple the catch
        for (int i = 0; i < 2; i++) {
            player.getWorld().dropItemNaturally(player.getLocation(), caught.getItemStack().clone());
        }
        event.setExpToDrop(event.getExpToDrop() * 3);
    }

    @Override public String getDescription() { return "Complete oceanic dominion."; }
    @Override public String getDescription(int level) {
        return "§dConduit Power§7 near water + §atriple catches§7 + §eResistance II§7."; }
}
