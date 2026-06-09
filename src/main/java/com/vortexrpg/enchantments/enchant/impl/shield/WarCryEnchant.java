package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** War Cry: After blocking 5 hits, AoE fear making mobs flee. */
public class WarCryEnchant extends VortexEnchant {

    public WarCryEnchant() { super("war_cry", "War Cry", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        String key = "war_cry_blocks";
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), key) + 1;
        int threshold = cfgi("threshold", 6 - level);
        if (count < threshold) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, count);
            return;
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, 0);
        double radius = cfg("radius", 6.0 + level * 2);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            if (e instanceof Mob mob) {
                mob.setTarget(null);
                var away = e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(0.8).setY(0.2);
                e.setVelocity(away);
            }
        }
        SoundUtil.play(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.6f, 1.5f);
    }

    @Override public String getDescription() { return "After several blocks, AoE fear."; }
    @Override public String getDescription(int level) {
        return "§7After §e" + (6 - level) + "§7 blocks: §cmobs flee§7 in " + (int)(6 + level * 2) + "b."; }
}
