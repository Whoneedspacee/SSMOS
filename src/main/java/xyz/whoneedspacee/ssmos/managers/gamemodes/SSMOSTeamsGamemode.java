package xyz.whoneedspacee.ssmos.managers.gamemodes;

import xyz.whoneedspacee.ssmos.kits.original.*;
import xyz.whoneedspacee.ssmos.kits.ssmos.*;

public class SSMOSTeamsGamemode extends TeamsGamemode {

    public SSMOSTeamsGamemode() {
        super();
        this.name = "Super Smash Mobs OS Teams";
        this.short_name = "SSMOS2";
        this.description = new String[] {
                "Each player has 3 respawns",
                "Attack to restore hunger!",
                "Last player alive wins!"
        };
    }

    @Override
    public void updateAllowedKits() {
        allowed_kits.clear();
        allowed_kits.add(new KitSkeleton());
        allowed_kits.add(new KitIronGolem());
        allowed_kits.add(new OSKitSpider());
        allowed_kits.add(new KitSlime());
        allowed_kits.add(new KitSquid());
        allowed_kits.add(new KitCreeper());
        allowed_kits.add(new OSKitEnderman());
        allowed_kits.add(new OSKitSnowman());
        allowed_kits.add(new KitWolf());
        allowed_kits.add(new KitMagmaCube());
        allowed_kits.add(new KitWitch());
        allowed_kits.add(new KitWitherSkeleton());
        allowed_kits.add(new OSKitZombie());
        allowed_kits.add(new KitCow());
        allowed_kits.add(new OSKitSkeletonHorse());
        allowed_kits.add(new KitPig());
        allowed_kits.add(new OSKitBlaze());
        allowed_kits.add(new KitChicken());
        allowed_kits.add(new KitGuardian());
        allowed_kits.add(new KitSheep());
        allowed_kits.add(new KitVillager());
    }

}
