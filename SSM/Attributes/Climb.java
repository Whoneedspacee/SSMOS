package SSM.Attributes;

import SSM.Attributes.DoubleJumps.DoubleJump;
import SSM.GameManagers.KitManager;
import SSM.Utilities.BlocksUtil;
import SSM.Utilities.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class Climb extends Attribute {

    protected double power;
    protected boolean chargedDoubleJump;

    public Climb(double power) {
        super();
        this.name = "Spider Climb";
        this.expUsed = 1.0f/80.0f;
        this.power = power;
        this.chargedDoubleJump = false;
        task = this.runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public void run() {
        if (Utils.entityIsOnGround(owner)) {
            chargedDoubleJump = false;
        }
        if (!owner.isSneaking()) {
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        for (Block block : BlocksUtil.getBlocks(owner.getLocation(), 1)) {
            if (block.getType().isSolid()) {
                owner.setVelocity(new Vector(0, power, 0));
                if(!chargedDoubleJump) {
                    KitManager.getPlayerKit(owner).getAttributes().forEach(attribute -> {
                        if(attribute instanceof DoubleJump) {
                            DoubleJump dj = (DoubleJump) attribute;
                            dj.resetDoubleJumps();
                            chargedDoubleJump = true;
                        }
                    });
                }
                break;
            }
        }
    }

}
