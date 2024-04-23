package xyz.whoneedspacee.ssmos.managers.maps;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.File;

public class LobbyMap extends SmashMap {

    public LobbyMap(File file) {
        super(file);
        this.permanent_chunk_load_radius = 5;
    }

    @Override
    public void createWorld() {
        super.createWorld();
        if(world == null) {
            return;
        }
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Player) && entity instanceof LivingEntity) {
                entity.remove();
            }
        }
    }

}
