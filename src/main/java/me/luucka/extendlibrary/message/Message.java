package me.luucka.extendlibrary.message;

import me.luucka.extendlibrary.message.exception.UnknownMessageKeyException;
import me.luucka.extendlibrary.message.serializer.*;
import me.luucka.extendlibrary.util.IReload;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class Message implements IReload {

    private final JavaPlugin plugin;

    private final String fileName;

    private final File file;

    private final Map<Class<?>, TypeSerializer<?>> serializerMap = new HashMap<>();
    private final Map<String, String> messages = new HashMap<>();

    public Message(final JavaPlugin plugin, final String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName + ".properties");
        saveFile();
        reload();
        registerDefaultSerializers();
    }

    private void saveFile() {
        if (!file.exists()) {
            try {
                Files.copy(this.getClass().getResourceAsStream("/" + fileName + ".properties"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to create file " + file, e);
            }
        }
    }

    @Override
    public void reload() {
        messages.clear();
        URL[] urls;
        try {
            urls = new URL[]{plugin.getDataFolder().toURI().toURL()};
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        ResourceBundle resourceBundle = ResourceBundle.getBundle(fileName, Locale.getDefault(), new URLClassLoader(urls));
        resourceBundle.keySet().forEach(key -> messages.put(key, resourceBundle.getString(key)));
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
        return new MessageBuilder(serializerMap, getMessage("prefix"), getMessage(messageKey));
    }
}
