package ssm.attributes;

import ssm.utilities.DamageUtil;
import ssm.utilities.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Compass extends Attribute {

    public static ItemStack COMPASS_ITEM;

    public Compass() {
        super();
        this.name = "Tracking Compass";
        task = this.runTaskTimer(plugin, 0, 5);
        COMPASS_ITEM = new ItemStack(Material.COMPASS);
        ItemMeta meta = COMPASS_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + name);
        COMPASS_ITEM.setItemMeta(meta);
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        Location finalLocation = null;
        Player finalTarget = null;
        Location location = owner.getLocation();
        double finalDist = -1;
        for (Player target : owner.getWorld().getPlayers()) {
            if (target.equals(owner)) {
                continue;
            }
            if(!target.getWorld().equals(owner.getWorld())) {
                continue;
            }
            if(!DamageUtil.canDamage(target, owner)) {
                continue;
            }
            double dist = location.distance(target.getLocation());
            if (dist < finalDist || finalDist == -1) {
                finalDist = dist;
                finalLocation = target.getLocation();
                finalTarget = target;
            }
        }
        if (finalLocation == null) {
            return;
        }
        owner.setCompassTarget(finalLocation);
        double distance = location.distance(finalLocation);
        double height = finalLocation.getY() - location.getY();
        ChatColor team_color = ChatColor.YELLOW;
        if(owner.getItemInHand().getType() == Material.COMPASS) {
            String text = 	"    " + ChatColor.WHITE + ChatColor.BOLD + "Nearest Target: " + team_color + finalTarget.getDisplayName() +
                    "    " + ChatColor.WHITE + ChatColor.BOLD + "Distance: " + team_color + String.format("%.1f", distance) +
                    "    " + ChatColor.WHITE + ChatColor.BOLD + "Height: " + team_color + String.format("%.1f", height);
            Utils.sendActionBarMessage(text, owner);

        }
    }

}
