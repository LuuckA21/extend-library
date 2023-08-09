package me.luucka.extendlibrary.message;

import me.luucka.extendlibrary.message.exception.UnknownMessageKeyException;
import me.luucka.extendlibrary.message.serializer.*;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Message {
    private final Map<Class<?>, TypeSerializer<?>> serializerMap = new HashMap<>();
    private final Map<String, String> messages = new HashMap<>();

    public Message(final String fileName) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(fileName);
        resourceBundle.keySet().forEach(key -> messages.put(key, resourceBundle.getString(key)));
        registerDefaultSerializers();
    }

    public void addPrefix() {
        if (!messages.containsKey("prefix")) return;

        String prefix = messages.get("prefix");
        messages.replaceAll((k, v) -> v.replace("<prefix>", prefix));
    }

    private String getMessage(String messageKey) throws UnknownMessageKeyException {
        if (!messages.containsKey(messageKey)) {
            throw new UnknownMessageKeyException(messageKey);
        }
        return messages.get(messageKey);
    }

    public <T> void registerSerializer(Class<T> type, TypeSerializer<? super T> serializer) {
        serializerMap.put(type, serializer);
    }

    public void registerDefaultSerializers() {
        // Built-In Types
        registerSerializer(Boolean.class, new BooleanSerializer());
        registerSerializer(Character.class, new CharSerializer());
        registerSerializer(Double.class, new DoubleSerializer());
        registerSerializer(Float.class, new FloatSerializer());
        registerSerializer(Integer.class, new IntegerSerializer());
        registerSerializer(Long.class, new LongSerializer());
        registerSerializer(String.class, new StringSerializer());
        registerSerializer(TextComponent.class, new ComponentSerializer());

        // Minecraft Types
        registerSerializer(ItemStack.class, new ItemStackSerializer());
        registerSerializer(Location.class, new LocationSerializer());
        registerSerializer(OfflinePlayer.class, new OfflinePlayerSerializer());
        registerSerializer(Player.class, new PlayerSerializer());
        registerSerializer(World.class, new WorldSerializer());
    }

    public MessageBuilder from(String messageKey) {
        return new MessageBuilder(serializerMap, getMessage(messageKey));
    }
}
