package me.luucka.extendlibrary.message.serializer;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class OfflinePlayerSerializer implements TypeSerializer<OfflinePlayer> {
    @Override
    public @NotNull Component serialize(@NotNull OfflinePlayer obj) {
        return obj.getName() == null ? Component.text(obj.getUniqueId().toString()) : Component.text(obj.getName());
    }
}
