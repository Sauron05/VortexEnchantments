package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Cipher: Each consecutive hit by same attacker reduces damage by 2/3/5% (stacks up to 5). */
public class CipherEnchant extends VortexEnchant {
    private static final double[] PER_STACK = {0.02, 0.03, 0.05};
    private static final int MAX_STACKS = 5;

    public CipherEnchant() { super("cipher", "Cipher", EnchantRarity.EPIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        String attackerId = event.getDamager().getEntityId() + "";
        String key = "cipher_stacks_" + attackerId;
        int stacks = Math.min(plugin.getPlayerDataManager().getInt(player.getUniqueId(), key) + 1, MAX_STACKS);
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, stacks);
        double reduce = cfg("per_stack", PER_STACK[level-1]) * stacks;
        event.setDamage(event.getDamage() * (1.0 - reduce));
    }

    @Override public String getDescription() { return "Repeated hits from same attacker dealt less."; }
    @Override public String getDescription(int level) {
        return "§7Per consecutive hit: §a-" + (int)(PER_STACK[level-1]*100) + "§a%§7 damage (max 5 stacks)."; }
}
