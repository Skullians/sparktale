package me.lucko.spark.hytale;

import com.google.common.collect.ImmutableMap;
import com.hypixel.hytale.protocol.packets.connection.PongType;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import me.lucko.spark.common.monitor.ping.PlayerPingProvider;
import me.lucko.spark.hytale.util.PlayerUtil;

import java.util.Map;


public class HytalePlayerPingProvider implements PlayerPingProvider {
    @Override
    public Map<String, Integer> poll() {
        ImmutableMap.Builder<String, Integer> builder = ImmutableMap.builder();
        
        for (PlayerRef player : Universe.get().getPlayers()) {
            PacketHandler.PingInfo pingInfo = player.getPacketHandler().getPingInfo(PongType.Tick);
            builder.put(player.getUsername(), Math.toIntExact(pingInfo.getPingMetricSet().getLastValue()));
        }
        
        return builder.build();
    }
}
