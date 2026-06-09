package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Random;

/**
 * Moonfish — Fishing Rod (Rare, Max 3)
 * During night-time (time > 12000), increases rare catch chance and can yield glowing fish (prismarine).
 */
public class MoonfishEnchant extends VortexEnchant {

    private static final Random RNG = new Random();

    public MoonfishEnchant() {
        super("moonfish", "Moonfish", "fishingrod");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        return "At night, §e+15/20/30%§7 chance for rare catches + may yield §bglow fish§7.";
    }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        long time = player.getWorld().getTime();
        if (time < 12000) return; // Daytime

        double[] chances = {0.15, 0.20, 0.30};
        if (RNG.nextDouble() < cfgd("night_bonus_chance", chances[level - 1])) {
            Material reward = RNG.nextBoolean() ? Material.GLOW_INK_SAC : Material.PRISMARINE_CRYSTALS;
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(reward));
            player.sendMessage("§9[Moonfish] §bA creature of the deep answers the moon's call!");
        }
    }
}
