package me.lucko.spark.hytale.util;

import com.hypixel.hytale.server.core.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import javax.annotation.Nonnull;


public final class MessageUtil {

    @Nonnull
    public static Message from(@Nonnull Component component) {
        Message message = create(component);
        formatting(message, component);
        
        for (Component child : component.children()) {
            message.insert(from(child));
        }
        
        return message;
    }
    
    @Nonnull
    private static Message create(@Nonnull Component component) {
        if (component instanceof TextComponent) {
            String content = ((TextComponent) component).content();
            return content.isEmpty() ? Message.empty() : Message.raw(content);
        } else if (component instanceof TranslatableComponent) {
            TranslatableComponent translatableComponent = (TranslatableComponent) component;
            return Message.translation(translatableComponent.key());
        }
        
        return Message.empty();
    }
    
    private static void formatting(@Nonnull Message message, @Nonnull Component component) {
        TextColor color = component.color();
        if (color != null) {
            message.color(formatColor(color));
        }
        
        if (component.hasDecoration(TextDecoration.BOLD)) {
            message.bold(component.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE);
        }

        if (component.hasDecoration(TextDecoration.ITALIC)) {
            message.italic(component.decoration(TextDecoration.ITALIC) == TextDecoration.State.TRUE);
        }

        if (component.hasDecoration(TextDecoration.OBFUSCATED)) {
            message.monospace(component.decoration(TextDecoration.OBFUSCATED) == TextDecoration.State.TRUE);
        }

        ClickEvent clickEvent = component.clickEvent();
        if (clickEvent != null && clickEvent.action() == ClickEvent.Action.OPEN_URL) {
            message.link(clickEvent.value());
        }
    }

    @Nonnull
    private static String formatColor(@Nonnull TextColor color) {
        if (color instanceof NamedTextColor) {
            NamedTextColor namedColor = (NamedTextColor) color;
            return String.format("#%06X", namedColor.value());
        } else {
            return String.format("#%06X", color.value());
        }
    }
}
