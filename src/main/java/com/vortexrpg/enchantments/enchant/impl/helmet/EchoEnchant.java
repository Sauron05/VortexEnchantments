package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/** Echo: After being hit, the next melee hit within 3s deals bonus damage. */
public class EchoEnchant extends VortexEnchant {
    private static final Map<UUID, Long> LAST_HIT = new HashMap<>();

    public EchoEnchant() { super("echo_helmet", "Echo", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        LAST_HIT.put(victim.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, org.bukkit.entity.LivingEntity victim, int level) {
        if (!isEnabled()) return;
        Long lastHit = LAST_HIT.remove(attacker.getUniqueId());
        if (lastHit == null) return;
        long window = cfgi("window_ms", 3000);
        if (System.currentTimeMillis() - lastHit > window) return;
        double bonus = cfgd("bonus_damage", 1.0 + level);
        event.setDamage(event.getDamage() + bonus);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SONIC_BOOM, 1, 0.2);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 0.3f, 2.0f);
    }

    @Override public String getDescription(int level) {
        return "§7After being hit, your next attack deals §a+" + (1.0 + level) + " §7bonus damage.";
    }
}
