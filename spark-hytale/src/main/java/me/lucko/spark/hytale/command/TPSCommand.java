package me.lucko.spark.hytale.command;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;


public class TPSCommand extends CommandBase {
    
    public TPSCommand() {
        super("tps", "Get the server's performance metrics");
    }

    @Override
    protected void executeSync(@NotNull CommandContext commandContext) {
        // todo
    }
}
