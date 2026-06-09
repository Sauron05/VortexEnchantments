package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

/**
 * Reap: Below Y=40 = double/triple/4× drops. Above Y=200 = double/triple/4× XP. Middle = normal.
 */
public class ReapEnchant extends VortexEnchant {

    private static final int[] DROP_MULT = {2, 3, 4};
    private static final int[] XP_MULT = {2, 3, 4};

    public ReapEnchant() {
        super("reap", "Reap", EnchantRarity.RARE, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        int underY = cfgi("underground_threshold", 40);
        int skyY = cfgi("sky_threshold", 200);
        int killedY = (int) killed.getLocation().getY();

        if (killedY < underY) {
            int mult = cfgi("drop_multiplier", DROP_MULT[level - 1]);
            var original = new java.util.ArrayList<>(event.getDrops());
            event.getDrops().clear();
            for (var item : original) {
                for (int i = 0; i < mult; i++) event.getDrops().add(item.clone());
            }
        } else if (killedY > skyY) {
            int xpMult = cfgi("xp_multiplier", XP_MULT[level - 1]);
            event.setDroppedExp(event.getDroppedExp() * xpMult);
        }
    }

    @Override
    public String getDescription() { return "Kills deep underground give more drops; high up give more XP."; }

    @Override
    public String getDescription(int level) {
        return "§7Below Y=40: §a" + DROP_MULT[level-1] + "× drops§7. Above Y=200: §b" + XP_MULT[level-1] + "× XP§7.";
    }
}
