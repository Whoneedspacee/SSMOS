package ssm.attributes;

import ssm.events.SmashDamageEvent;
import ssm.gamemanagers.ownerevents.OwnerDealSmashDamageEvent;
import ssm.gamemanagers.ownerevents.OwnerTakeSmashDamageEvent;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Stampede extends Attribute implements OwnerDealSmashDamageEvent, OwnerTakeSmashDamageEvent {

    private long start_time_ms = 0;
    private int ticks = 0;
    protected int stack_increase_time_ms = 3000;
    protected int stacks = -1;
    protected int max_stacks = 3;
    protected double stop_sprint_damage = 3;

    public Stampede() {
        super();
        this.name = "Stampede";
        this.usage = AbilityUsage.PASSIVE;
        this.description = new String[] {
                ChatColor.RESET + "As you sprint, you will slowly",
                ChatColor.RESET + "build up Speed Levels. You attacks",
                ChatColor.RESET + "will deal extra damage and knockback",
                ChatColor.RESET + "while you have Speed.",
        };
        this.runTaskTimer(plugin, 0L, 0L);
    }

    @Override
    public void run() {
        if(owner == null) {
            this.cancel();
            return;
        }
        if(!check()) {
            return;
        }
        if(stacks > 0) {
            Utils.playParticle(EnumParticle.CRIT, owner.getLocation(), (float) (Math.random() - 0.5), 0.2f + (float) Math.random(),
                    (float) (Math.random() - 0.5), 0, stacks * 2, 96, owner.getWorld().getPlayers());
        }
        ticks = (ticks + 1) % 5;
        if(ticks != 0) {
            return;
        }
        if(stacks == -1) {
            if(owner.isSprinting() || !owner.getLocation().getBlock().isLiquid())  {
                start_time_ms = System.currentTimeMillis();
                stacks = 0;
            }
            return;
        }
        if(!owner.isSprinting() || owner.getLocation().getBlock().isLiquid()) {
            removeStampede();
            return;
        }
        if(stacks > 0) {
            owner.removePotionEffect(PotionEffectType.SPEED);
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 38, stacks - 1, false, false));
        }
        if(System.currentTimeMillis() - start_time_ms < stack_increase_time_ms) {
            return;
        }
        start_time_ms = System.currentTimeMillis();
        if(stacks < max_stacks) {
            stacks++;
            owner.getWorld().playSound(owner.getLocation(), Sound.COW_HURT, 2f, 0.75f + 0.25f * stacks);
        }
    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if(e.isCancelled()) {
            return;
        }
        if(e.getDamageCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if(stacks <= 0) {
            return;
        }
        e.setDamage(e.getDamage() + stacks);
        e.multiplyKnockback(1 + 0.1 * stacks);
        e.setReason(name + ", Attack");
        if(e.getDamagee() instanceof Player) {
            Player player = (Player) e.getDamagee();
            Utils.sendAttributeMessage(ChatColor.YELLOW + owner.getName() +
                    ChatColor.GRAY + " hit you with", name, player, ServerMessageType.GAME);
        }
        owner.getWorld().playSound(owner.getLocation(), Sound.ZOMBIE_WOOD, 1f, 2f);
        owner.getWorld().playSound(owner.getLocation(), Sound.COW_HURT, 2f, 2f);
        removeStampede();
    }

    @Override
    public void onOwnerTakeSmashDamageEvent(SmashDamageEvent e) {
        if(e.isCancelled()) {
            return;
        }
        if(e.getDamage() < stop_sprint_damage) {
            return;
        }
        removeStampede();
    }

    public void removeStampede() {
        stacks = -1;
        owner.removePotionEffect(PotionEffectType.SPEED);
    }

    public void activate() {
        return;
    }

}
