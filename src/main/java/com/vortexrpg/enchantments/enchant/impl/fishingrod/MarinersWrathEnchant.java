package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** MarinersWrath: In thunderstorms, catches are doubled and hooked entities take lightning. */
public class MarinersWrathEnchant extends VortexEnchant {

    public MarinersWrathEnchant() { super("mariners_wrath", "Mariner's Wrath", EnchantRarity.EPIC, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.getWorld().isThundering()) return;
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getCaught() instanceof Item caught) {
            ItemStack extra = caught.getItemStack().clone();
            player.getWorld().dropItemNaturally(player.getLocation(), extra);
        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY && event.getCaught() instanceof LivingEntity target) {
            target.getWorld().strikeLightningEffect(target.getLocation());
            target.damage(cfgd("lightning_damage", 4.0 + level * 2), player);
        }
    }

    @Override public String getDescription() { return "Thunderstorm empowers your fishing rod."; }
    @Override public String getDescription(int level) {
        return "§7In storms: §adouble catches§7 + §elightning§7 on hooked mobs (§c" + (int)(4.0 + level * 2) + "§7 dmg)."; }
}
