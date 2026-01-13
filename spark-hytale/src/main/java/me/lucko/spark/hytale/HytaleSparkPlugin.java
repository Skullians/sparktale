package me.lucko.spark.hytale;

import com.hypixel.hytale.common.plugin.AuthorInfo;
import com.hypixel.hytale.server.core.command.system.CommandRegistration;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.Universe;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.SparkPlugin;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.monitor.ping.PlayerPingProvider;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.platform.serverconfig.ServerConfigProvider;
import me.lucko.spark.common.platform.world.WorldInfoProvider;
import me.lucko.spark.common.sampler.ThreadDumper;
import me.lucko.spark.common.sampler.source.SourceMetadata;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.util.SparkThreadFactory;
import me.lucko.spark.hytale.util.PlayerUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class HytaleSparkPlugin extends JavaPlugin implements SparkPlugin {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4, new SparkThreadFactory());
    private final ThreadDumper gameThreadDumper =
            new ThreadDumper.Regex(
                    new HashSet<>(Arrays.asList(
                            "WorldThread - .*",
                            "WorldMap - .*",
                            "ChunkLighting - .*"
                    ))
            );
    
    private SparkPlatform platform;
    private CommandRegistration command;
    
    public HytaleSparkPlugin(@NotNull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.platform = new SparkPlatform(this);
        this.platform.enable();

        this.command = getCommandRegistry().registerCommand(new HytaleSparkCommand(this.platform));
    }

    @Override
    protected void shutdown() {
        if (this.command != null) {
            this.command.unregister();
            this.command = null;
        }
        
        if (this.platform != null) {
            this.platform.disable();
            this.platform = null;
        }
        
        this.scheduler.shutdown();
    }

    @Override
    public String getVersion() {
        return getManifest().getVersion().toString();
    }

    @Override
    public Path getPluginDirectory() {
        return getDataDirectory();
    }

    @Override
    public String getCommandName() {
        return "spark";
    }

    @Override
    public Stream<? extends CommandSender> getCommandSenders() {
        return Stream.concat(
                Universe.get().getPlayers().stream().map(PlayerUtil::asPlayer), 
                Stream.of(ConsoleSender.INSTANCE)
        ).map(HytaleCommandSender::new);
    }

    @Override
    public void executeAsync(Runnable task) {
        this.scheduler.execute(task);
    }
    
    @Override
    public void executeSync(Runnable task) {
        Universe.get().getDefaultWorld().execute(task); // TODO
    }

    @Override
    public PlatformInfo getPlatformInfo() {
        return new HytalePlatformInfo();
    }

    @Override
    public void log(Level level, String msg) {
        getLogger().at(level).log(msg);
    }

    @Override
    public void log(Level level, String msg, Throwable throwable) {
        getLogger().at(level).withCause(throwable).log(msg);
    }

    @Override
    public ThreadDumper getDefaultThreadDumper() {
        return gameThreadDumper;
    }

    @Override
    public TickHook createTickHook() {
        // todo
    }

    @Override
    public Collection<SourceMetadata> getKnownSources() {
        return SourceMetadata.gather(
                PluginManager.get().getPlugins(),
                PluginBase::getName,
                plugin -> plugin.getManifest().getVersion().toString(),
                plugin -> plugin.getManifest().getAuthors().stream().map(AuthorInfo::getName).collect(Collectors.joining(", ")),
                plugin -> plugin.getManifest().getDescription()
        );
    }

    @Override
    public PlayerPingProvider createPlayerPingProvider() {
        return new HytalePlayerPingProvider();
    }

    @Override
    public ServerConfigProvider createServerConfigProvider() {
        return new HytaleServerConfigProvider();
    }

    @Override
    public WorldInfoProvider createWorldInfoProvider() {
        
    }
}
