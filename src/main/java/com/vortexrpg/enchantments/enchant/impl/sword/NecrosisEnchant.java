package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Necrosis: Hits apply "Necrosis" stacks. At 3 stacks, target takes a burst
 * of 4/6/8 hearts of wither damage and stacks reset.
 */
public class NecrosisEnchant extends VortexEnchant {

    private final ConcurrentHashMap<UUID, Integer> necrosisStacks = new ConcurrentHashMap<>();

    public NecrosisEnchant() {
        super("necrosis", "Necrosis", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int requiredStacks = cfgi("required_stacks", 3);
        double burstDamage = cfgd("burst_damage", 6.0 + level * 2.0);

        UUID victimId = victim.getUniqueId();
        int stacks = necrosisStacks.getOrDefault(victimId, 0) + 1;

        if (stacks >= requiredStacks) {
            necrosisStacks.remove(victimId);

            victim.damage(burstDamage, attacker);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, level - 1, false, true));

            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SCULK_SOUL, 20, 0.5);
            SoundUtil.play(victim.getLocation(), Sound.ENTITY_WITHER_HURT, 0.8f, 1.5f);

            attacker.sendMessage("§4[Necrosis] §7Burst! §c" + String.format("%.1f", burstDamage / 2) + " hearts §7of wither damage!");
            if (victim instanceof Player p) {
                p.sendMessage("§4[Necrosis] §7Your flesh rots away!");
            }
        } else {
            necrosisStacks.put(victimId, stacks);
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SMOKE, stacks * 3, 0.3);

            if (victim instanceof Player p) {
                p.sendMessage("§4[Necrosis] §7Stack " + stacks + "/" + requiredStacks);
            }
        }
    }

    @Override
    public String getDescription(int level) {
        double hearts = (6.0 + level * 2.0) / 2;
        return "§7Apply necrosis stacks. At §c3 stacks§7: burst for §c" + String.format("%.1f", hearts) + "\u2764§7 wither damage.";
    }
}
