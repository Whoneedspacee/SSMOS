package ssm.managers.maps;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.File;

public class LobbyMap extends SmashMap {

    public LobbyMap(File file) {
        super(file);
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
