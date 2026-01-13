package me.lucko.spark.hytale;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.command.Command;
import me.lucko.spark.hytale.command.TPSCommand;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;


public class HytaleSparkCommand extends AbstractCommandCollection {

    private final SparkPlatform platform;

    public HytaleSparkCommand(SparkPlatform platform) {
        super("spark", "Command for spark");

        this.platform = platform;

        setAllowsExtraArguments(true);
        
    }

    @Override
    protected @Nullable CompletableFuture<Void> execute(@NonNull CommandContext ctx) {
        String[] args = ctx.getInputString().split(" ");
        return this.platform.executeCommand(HytaleCommandSender.of(ctx.sender()), Arrays.copyOfRange(args, 1, args.length));
    }
}
