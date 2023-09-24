package me.luucka.extendlibrary.message;

import me.luucka.extendlibrary.message.exception.MissingSerializerException;
import me.luucka.extendlibrary.message.serializer.TypeSerializer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.time.temporal.TemporalAccessor;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MessageBuilder {
    private final Map<Class<?>, TypeSerializer<?>> serializerMap;
    private final String message;

    private final Set<TagResolver> replacements = new HashSet<>();

    public MessageBuilder(Map<Class<?>, TypeSerializer<?>> serializerMap, String message, String prefix) {
        this.serializerMap = serializerMap;
        this.message = message.replace("<prefix>", prefix);
    }

    public <T> MessageBuilder with(String key, T value) {
        TypeSerializer<T> serializer1 = (TypeSerializer<T>) serializerMap.get(value.getClass());

        if (serializer1 == null) {
            Class<?>[] interfaces = value.getClass().getInterfaces();

            for (Class<?> interfaze : interfaces) {
                if (serializerMap.containsKey(interfaze)) {
                    serializer1 = (TypeSerializer<T>) serializerMap.get(interfaze);
                    break;
                }
            }

            if (serializer1 == null) {
                throw new MissingSerializerException(value.getClass());
            }
        }

        TypeSerializer<T> serializer = serializer1;
        replacements.add(Placeholder.component(key, serializer.serialize(value)));
        return this;
    }

    public MessageBuilder withNumber(String key, Number value) {
        replacements.add(Formatter.number(key, value));
        return this;
    }

    public MessageBuilder withDate(String key, TemporalAccessor value) {
        replacements.add(Formatter.date(key, value));
        return this;
    }

    public MessageBuilder withChoice(String key, Number value) {
        replacements.add(Formatter.choice(key, value));
        return this;
    }

    public MessageBuilder withBool(String key, boolean value) {
        replacements.add(Formatter.booleanChoice(key, value));
        return this;
    }

    public Component build() {
        TagResolver tagResolver = TagResolver.builder().resolvers(replacements).build();
        return MiniMessage.miniMessage().deserialize(message, tagResolver);
    }

    public void send(Audience audience) {
        audience.sendMessage(this::build);
    }
}
