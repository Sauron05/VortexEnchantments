package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** DraftDancer: Jump Boost after landing from a glide. */
public class DraftDancerEnchant extends VortexEnchant {

    public DraftDancerEnchant() { super("draft_dancer", "Draft Dancer", EnchantRarity.RARE, 3, List.of(ItemTarget.ELYTRA)); }

    @SuppressWarnings("deprecation")
    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        boolean wasGliding = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "dd_gl", 0) == 1;
        boolean isGliding = player.isGliding();
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "dd_gl", isGliding ? 1 : 0);
        if (wasGliding && !isGliding && player.isOnGround()) {
            int amp = Math.min(level, 3);
            int duration = cfgi("duration", 2 + level) * 20;
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, duration, amp, true, false, true));
        }
    }

    @Override public String getDescription() { return "Jump Boost after landing from flight."; }
    @Override public String getDescription(int level) {
        return "§7Grants §aJump Boost " + (level + 1) + "§7 for §e" + (2 + level) + "s§7 after landing."; }
}
