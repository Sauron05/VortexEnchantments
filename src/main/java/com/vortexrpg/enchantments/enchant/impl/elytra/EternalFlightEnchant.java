package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** EternalFlight: Elytra never loses durability; passive regen while gliding. */
public class EternalFlightEnchant extends VortexEnchant {

    public EternalFlightEnchant() { super("eternal_flight", "Eternal Flight", EnchantRarity.MYTHIC, 1, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        // Restore any durability lost this tick
        org.bukkit.inventory.ItemStack chest = player.getInventory().getChestplate();
        if (chest != null && chest.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable dmg && dmg.getDamage() > 0) {
            dmg.setDamage(0);
            chest.setItemMeta(dmg);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0, true, false, true));
    }

    @Override public String getDescription() { return "Elytra immune to damage; passive regen while gliding."; }
    @Override public String getDescription(int level) {
        return "§dElytra never breaks§7. Passive §aRegeneration I§7 while gliding."; }
}
