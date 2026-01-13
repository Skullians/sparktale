package me.lucko.spark.hytale;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import me.lucko.spark.common.command.sender.AbstractCommandSender;
import me.lucko.spark.hytale.util.MessageUtil;
import net.kyori.adventure.text.Component;

import java.util.UUID;


public class HytaleCommandSender extends AbstractCommandSender<CommandSender> {
    
    public HytaleCommandSender(CommandSender delegate) {
        super(delegate);
    }

    @Override
    public String getName() {
        return delegate.getDisplayName();
    }

    @Override
    public UUID getUniqueId() {
        return delegate.getUuid();
    }

    @Override
    public void sendMessage(Component message) {
        delegate.sendMessage(MessageUtil.from(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return delegate.hasPermission(permission);
    }
}
