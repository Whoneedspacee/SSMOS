package xyz.whoneedspacee.ssmos.kits.ssmos;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import xyz.whoneedspacee.ssmos.abilities.original.BoneExplosion;
import xyz.whoneedspacee.ssmos.abilities.original.RopedArrow;
import xyz.whoneedspacee.ssmos.attributes.*;
import xyz.whoneedspacee.ssmos.attributes.bowcharge.Barrage;
import xyz.whoneedspacee.ssmos.attributes.doublejumps.GenericDoubleJump;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.utilities.SkinsUtil;

public class OSKitIllusioner extends Kit {

    public SkinsUtil util = null;
    
    public OSKitIllusioner() {
        super();
        this.damage = 5;
        this.armor = 6;
        this.regeneration = 0.2;
        this.knockback = 1.25;
        this.name = "Illusioner";
        this.menuItem = Material.BOW;
        this.podium_mob_type = EntityType.VILLAGER;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmorSlot(Material.CHAINMAIL_HELMET, 3);

        setAbility(new BoneExplosion(), 0);
        setAbility(new RopedArrow(), 1);
        setAbility(new BoneExplosion(), 0);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, Sound.GHAST_FIREBALL));
        addAttribute(new ItemGenerator(Material.ARROW, 1, 3, 3));
        addAttribute(new Barrage(1, 0.3, 5));
        addAttribute(new FixedArrowDamage(6));
        addAttribute(new MultiplyArrowKnockback(1.5));

        // Thanks mineskin.org
        String texture = "ewogICJ0aW1lc3RhbXAiIDogMTcwMjA4MjI4NTcwNSwKICAicHJvZmlsZUlkIiA6ICJmNjc4MTU4MDEwNjI0MjU0YmFjYjIwM2UzMWU5ZWVhNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJDaGVycmlQaWUzNCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lNGZlMTE4Y2ExYTc1YmQ2Yjk4NWEyMjNmN2ZkMmEyM2JlMDRiODg3ZWM5MGJhNzkyN2YzOGQxNTljMjUwMThjIgogICAgfQogIH0KfQ==";
        String signature = "fRkAks27uWMzFtKhaJEFpM9mC0lg/8NU81EKJQ3rziOOmZNzUrzRtKlQqcXRwS742ledW8k3bsYR3pcv4Ye4ZQQ+7MPas67Ma71ksGn9oCgk9lN5/0NomtnXRjwzrtLmDo7Yv1EVRXUfsjCLrLa9U839kQ0dZOwM0L/xQRCZxa1u775E76Z42t3cTawoV7p7VbZKaLhftzUZt5A2adZGCfO0nQSRWZz2YufZzsBZTpKT4lkyWvaDlceYhlEvZw04IAZEPXJtzpZ1OBmixWEQC5U5upBnhvJuu/zuQBO8WcTuf1dNSY5m0BILidRbrQcZDNj/riJdnYPFHq6OcZ9cEJSGjKercAIDfQ1CJh54a/yANyp7YUjq1W+hLV2C3DvfK16UTD/7HekjU6AfEpQk/+oSfRjyCeAHq5+/+iF1Sx17CNBly4WNQ5ABf+EhKZ93InVHoSn5O4cu7Mx5Dk5BhkBoYmYpYWg3gCRHI+xGJG3QIIOFZ41eXA3ALaP7kCRO1Ma/1+0Lfz5/hxoxnTLGgLexmFSXvLwh9BISu1gOZJnBYe32+wBypOvmKxxqLOHs6CwNEMc5C7FIP+OvzsEZVsW6mdoFvHxwLe3bjf3dLzZiE1/hyO6tWko6bX9AU6HC0HzNhLSYmb4gtFy/Q7UmG3a85MVDEEhvygkvLyR+cq4=";
        util = new SkinsUtil(owner);
        util.changeSkin(texture, signature);
    }

    @Override
    public void destroyKit() {
        super.destroyKit();
        util.removeSkin();
        util = null;
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.BOW), 1);
        setItem(new ItemStack(Material.ARROW), 2, getAttributeByClass(Barrage.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.BOW), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
