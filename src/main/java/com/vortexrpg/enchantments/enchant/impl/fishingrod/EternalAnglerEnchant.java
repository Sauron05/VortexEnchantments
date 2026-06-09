package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;

/** EternalAngler: Rod never breaks, catches always max quality, permanent luck. */
public class EternalAnglerEnchant extends VortexEnchant {

    public EternalAnglerEnchant() { super("eternal_angler", "Eternal Angler", EnchantRarity.MYTHIC, 1, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        // Keep rod at full durability
        ItemStack rod = player.getInventory().getItemInMainHand();
        if (rod.getItemMeta() instanceof Damageable dmg && dmg.getDamage() > 0) {
            dmg.setDamage(0);
            rod.setItemMeta(dmg);
        }
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item caught)) return;
        // Max stack quality
        ItemStack stack = caught.getItemStack();
        stack.setAmount(Math.max(stack.getAmount(), stack.getMaxStackSize() > 1 ? 3 : 1));
        caught.setItemStack(stack);
        // Triple XP
        event.setExpToDrop(event.getExpToDrop() * 3);
    }

    @Override public String getDescription() { return "Ultimate fishing mastery."; }
    @Override public String getDescription(int level) {
        return "§dRod never breaks§7. Catches are §amax quality§7 + §e3x XP§7."; }
}
