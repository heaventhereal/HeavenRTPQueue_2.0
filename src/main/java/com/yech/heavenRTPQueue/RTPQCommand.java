package com.yech.heavenRTPQueue;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static com.yech.heavenRTPQueue.RandomLocationGenerator.generateRandomLocation;
import static org.bukkit.Bukkit.getServer;

public class RTPQCommand extends BukkitCommand {
    private final HeavenRTPQueue plugin;
    private final ObjectArrayList<UUID> playersInQueue = new ObjectArrayList<>();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    protected RTPQCommand(HeavenRTPQueue plugin) {
        super("rtpqueue");
        this.setAliases(ObjectArrayList.of("rtpq"));
            this.plugin = plugin;
    }

    // World world = this.plugin.getConfig().getString("")

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) return false;

        String noperms = this.plugin.getConfig().getString("messages.no-perms");
        assert noperms != null;
        Component noPerms = miniMessage.deserialize(noperms);

        String leftRtp = this.plugin.getConfig().getString("messages.left-rtpq");
        assert leftRtp != null;
        Component componentMessage = miniMessage.deserialize(leftRtp);

        String globalleftRtpqmessage = this.plugin.getConfig().getString("messages.global-left-rtpq");
        assert globalleftRtpqmessage != null;
        globalleftRtpqmessage = globalleftRtpqmessage.replace("%player%", player.getName());
        Component globalComponentMessage = miniMessage.deserialize(globalleftRtpqmessage);

        String joinRtpq = this.plugin.getConfig().getString("messages.joined-rtpq");
        assert joinRtpq != null;
        Component componentJoinMessage = miniMessage.deserialize(joinRtpq);

        String globalRtpqmessage = this.plugin.getConfig().getString("messages.global-joined-rtpq");
        assert globalRtpqmessage != null;
        globalRtpqmessage = globalRtpqmessage.replace("%player%", player.getName());
        Component globalJoinMessage = miniMessage.deserialize(globalRtpqmessage);

        String teleportation = this.plugin.getConfig().getString("messages.being-teleported");
        assert teleportation != null;
        Component teleportMessage = miniMessage.deserialize(teleportation);

        if (commandSender.hasPermission("heaven.rtpq")) {
            if (playersInQueue.contains(player.getUniqueId())) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    playersInQueue.remove(player.getUniqueId());
                    player.sendMessage(componentMessage);
                    player.sendActionBar(componentMessage);

                    for (Player a : Bukkit.getOnlinePlayers()) {
                        if (!a.equals(commandSender)) {
                            a.sendMessage(globalComponentMessage);
                        }
                    }
                });
            } else {
                playersInQueue.add(player.getUniqueId());

                if (playersInQueue.size() == 2) {
                    List<String> worlds = this.plugin.getConfig().getStringList("worlds");

                    long seed = System.nanoTime();
                    nnrandomxoroshiro128plus random = new nnrandomxoroshiro128plus(seed);

                    int n = random.nextInt(worlds.size());
                    String worldName = worlds.get(n);

                    World world = Bukkit.getWorld(worldName);

                    assert world != null;
                    Location loc = generateRandomLocation(world, plugin);

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

                        Player player1 = Bukkit.getPlayer(playersInQueue.get(0));
                        Player player2 = Bukkit.getPlayer(playersInQueue.get(1));

                        assert player1 != null;
                        assert player2 != null;

                        player1.sendMessage(teleportMessage);
                        player2.sendMessage(teleportMessage);
                        player1.sendActionBar(teleportMessage);
                        player2.sendActionBar(teleportMessage);
                        player1.playSound(player1.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5.0F, 1F);
                        player2.playSound(player2.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5.0F, 1F);

                        player1.teleportAsync(loc);
                        player2.teleportAsync(loc);
                        player1.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1F);
                        player2.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1F);

                        playersInQueue.remove(player1.getUniqueId());
                        playersInQueue.remove(player2.getUniqueId());

                    });

                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        player.sendMessage(componentJoinMessage);
                        player.sendActionBar(componentJoinMessage);

                        for (Player a : Bukkit.getOnlinePlayers()) {
                            if (!a.equals(commandSender)) {
                                a.sendMessage(globalJoinMessage);
                            }
                        }
                    });
                }
            }

        } else Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> player.sendMessage(noPerms));
        return false;
    }
}