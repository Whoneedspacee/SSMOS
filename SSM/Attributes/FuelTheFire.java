package SSM.Attributes;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.Disguise;
import SSM.GameManagers.Disguises.MagmaCubeDisguise;
import SSM.GameManagers.KitManager;
import SSM.GameManagers.OwnerEvents.OwnerDealSmashDamageEvent;
import SSM.GameManagers.OwnerEvents.OwnerKillEvent;
import SSM.GameManagers.OwnerEvents.OwnerTakeSmashDamageEvent;
import SSM.Kits.Kit;
import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class FuelTheFire extends Attribute implements OwnerKillEvent, OwnerTakeSmashDamageEvent, OwnerDealSmashDamageEvent {

    private int stacks = 0;
    private int max_stacks = 3;

    public FuelTheFire() {
        super();
        this.name = "Fuel the Fire";
        this.usage = AbilityUsage.PASSIVE;
        this.description = new String[] {
                ChatColor.RESET + "Each kill increases your size, damage,",
                ChatColor.RESET + "armor and decreases your knockback taken.",
                ChatColor.RESET + "Resets on death.",
        };
        this.runTaskTimer(plugin, 0L, 5L);
    }

    public void activate() {
        return;
    }

    @Override
    public void run() {
        if(owner == null || !owner.isValid()) {
            this.cancel();
            return;
        }
        int real_size = stacks + 1;
        Utils.playParticle(EnumParticle.LAVA, owner.getLocation().add(0, 0.4, 0),
                0.15f * real_size, 0.15f * real_size, 0.15f * real_size, 0, 1, 96, owner.getWorld().getPlayers());
    }

    @Override
    public void onOwnerKillEvent(Player player) {
        if(player.equals(owner)) {
            return;
        }
        stacks = Math.min(stacks + 1, max_stacks);
        Disguise disguise = DisguiseManager.disguises.get(owner);
        if (!(disguise instanceof MagmaCubeDisguise)) {
            return;
        }
        MagmaCubeDisguise magmaCubeDisguise = (MagmaCubeDisguise) disguise;
        magmaCubeDisguise.setSize(stacks + 1);
        Kit kit = KitManager.getPlayerKit(owner);
        if(kit == null) {
            return;
        }
        if(stacks == 1) {
            kit.setArmorSlot(Material.IRON_BOOTS, 0);
            kit.setArmor(4.5);
        }
        else if(stacks == 2) {
            kit.setArmorSlot(Material.IRON_CHESTPLATE, 2);
            kit.setArmor(5);
        }
        else if(stacks == 3) {
            kit.setArmorSlot(Material.DIAMOND_HELMET, 3);
            kit.setArmor(5.5);
        }
        owner.setExp(0.99F * (stacks / (float) max_stacks));
    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if(e.getDamageCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if(e.getDamager() == null) {
            return;
        }
        if(!(e.getDamager().equals(owner))) {
            return;
        }
        e.setDamage(e.getDamage() + stacks);
    }

    @Override
    public void onOwnerTakeSmashDamageEvent(SmashDamageEvent e) {
        if(e.getDamageCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if(e.getDamagee() == null) {
            return;
        }
        if(!(e.getDamagee().equals(owner))) {
            return;
        }
        if(stacks <= 0) {
            return;
        }
        e.multiplyKnockback(stacks * 0.15);
    }

}
