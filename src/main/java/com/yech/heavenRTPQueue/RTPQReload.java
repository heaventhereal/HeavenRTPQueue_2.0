package com.yech.heavenRTPQueue;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

public class RTPQReload extends BukkitCommand {

    HeavenRTPQueue plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public RTPQReload(HeavenRTPQueue plugin) {
        super("rtpqreload");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String configreload = this.plugin.getConfig().getString("messages.config-reloaded", "<green>Config reloaded.</green>");
            Component configReload = miniMessage.deserialize(configreload);

            commandSender.sendMessage(configReload);

            this.plugin.reloadConfig();
        });
        return false;
    }
}
