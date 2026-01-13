package me.lucko.spark.hytale.util;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jetbrains.annotations.Nullable;


public class PlayerUtil {
    
    @Nullable
    public static Player asPlayer(PlayerRef ref) {
        return ref.getReference().getStore().getComponent(ref.getReference(), Player.getComponentType());
    }
}
