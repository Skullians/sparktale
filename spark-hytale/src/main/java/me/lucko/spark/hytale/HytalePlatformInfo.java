package me.lucko.spark.hytale;

import com.hypixel.hytale.server.core.universe.Universe;
import me.lucko.spark.common.platform.PlatformInfo;


public class HytalePlatformInfo implements PlatformInfo {
    
    @Override
    public Type getType() {
        return Type.SERVER;
    }

    @Override
    public String getName() {
        return "Hytale";
    }

    @Override
    public String getBrand() {
        return Universe.get().getName();
    }

    @Override
    public String getVersion() {
        return Universe.get().getManifest().getVersion().toString();
    }

    @Override
    public String getMinecraftVersion() {
        return Universe.get().getManifest().getVersion().toString();
    }
}
